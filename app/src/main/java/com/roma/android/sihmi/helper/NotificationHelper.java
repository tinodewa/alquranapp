package com.roma.android.sihmi.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.RemoteMessage;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.notification.Message;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.ChatActivity;
import com.roma.android.sihmi.view.activity.SplashActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {
    public static  final String TYPE_PERSONAL = "__type_personal__";
    private static HashMap<String, List<Message>> MESSAGE;
    private static String CHANNEL_ID = "com.example.root.sihmi";
    public static final int ID_CHAT = 1;
    private static final int ID_GENERAL = 2;
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";

    public static void sendNotification(RemoteMessage remoteMessage, Contact contact, Context context) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String type = remoteMessage.getData().get("type");

        if (type != null) {
            String TYPE_GROUP = "__type_group__";
            if ((type.equals(TYPE_PERSONAL) || type.equals(TYPE_GROUP) && !contact.isBisukan())) {
                Message message = new Message(contact.getNama_panggilan(), (body != null) ? Tools.convertUTF8ToString(body) : "", System.currentTimeMillis());
                sendNotificationMessaging(message, context, contact, type.equals(TYPE_GROUP));
            }
            else {
                // force general
                sendNotificationGeneral(title, body, context, contact);
            }
        }
        else {
            // force general
            sendNotificationGeneral(title, body, context, contact);
        }
    }

    private static void sendNotificationMessaging(Message message, Context context, Contact contact, boolean isGroup) {
        String key = contact.get_id()+"_"+ID_CHAT;
        if (MESSAGE == null) {
            MESSAGE = new HashMap<>();
        }

        List<Message> messages = MESSAGE.get(key);

        if (messages == null) {
            messages = new ArrayList<>();
        }

        Intent intent = new Intent(context, ChatActivity.class)
                .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        messages.add(message);
        MESSAGE.put(key, messages);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        
        Notification notification;

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("REMOVE MESSAGE", "REMOVE MESSAGE " + key);
                removeHistoryMessage(key);
                context.unregisterReceiver(this);
            }
        };

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent cancelIntent = new Intent(NOTIFICATION_DELETED_ACTION);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);

        context.registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));
                
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER oreo");
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SIHMI", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle(context.getString(R.string.you));
            messagingStyle.setConversationTitle(context.getString(R.string.message_from) + contact.getNama_panggilan());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                messagingStyle.setGroupConversation(isGroup);
            }

            for (Message m : messages) {
                Notification.MessagingStyle.Message mMessage;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    String imageUrl = contact.getImage();


                    Person person;
                    if (imageUrl != null && imageUrl.trim().length() != 0) {
                        try {
                            Bitmap bitmap = Glide.with(context)
                                    .asBitmap()
                                    .load(imageUrl)
                                    .apply(new RequestOptions().circleCrop())
                                    .submit(50, 50)
                                    .get();

                            person = new Person.Builder()
                                    .setName(m.getSender())
                                    .setKey(contact.get_id())
                                    .setIcon(Icon.createWithBitmap(bitmap))
                                    .build();
                        } catch (Exception e) {
                            e.printStackTrace();
                            person = new Person.Builder()
                                    .setName(m.getSender())
                                    .setKey(contact.get_id())
                                    .build();
                        }
                    }
                    else {
                        person = new Person.Builder()
                                .setName(m.getSender())
                                .setKey(contact.get_id())
                                .build();
                    }


                    mMessage = new Notification.MessagingStyle.Message(m.getText(), m.getTimestamp(), person);
                }
                else {
                    mMessage = new Notification.MessagingStyle.Message(m.getText(), m.getTimestamp(), m.getSender());
                }
                messagingStyle.addMessage(mMessage);
            }

            notification = new Notification.Builder(context, notificationChannel.getId())
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setStyle(messagingStyle)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(cancelPendingIntent)
                    .setAutoCancel(true)
                    .build();
        }
        else {
            Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER not oreo");
            //noinspection deprecation
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(context.getString(R.string.you));
            messagingStyle.setConversationTitle(context.getString(R.string.message_from) + contact.getNama_panggilan());
            messagingStyle.setGroupConversation(isGroup);

            for (Message m : messages) {
                //noinspection deprecation
                NotificationCompat.MessagingStyle.Message mMessage = new NotificationCompat.MessagingStyle.Message(m.getText(), m.getTimestamp(), m.getSender());
                messagingStyle.addMessage(mMessage);
            }

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setStyle(messagingStyle)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(cancelPendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        notificationManager.notify(contact.get_id(), ID_CHAT, notification);
    }

    private static void sendNotificationGeneral(String title, String body, Context context, Contact contact) {
        Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER general");
        Intent intent;

        if (contact != null) {
            body = contact.getNama_panggilan() + " : " + body;
            intent = new Intent(context, ChatActivity.class).putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id());
        }
        else {
            intent = new Intent(context, SplashActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SIHMI", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

            notification = new Notification.Builder(context, notificationChannel.getId())
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setContentTitle(title)
                    .setContentText(Tools.convertUTF8ToString(body))
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }
        else {
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setContentTitle(title)
                    .setContentText(Tools.convertUTF8ToString(body))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        notificationManager.notify(ID_GENERAL, notification);
    }

    public static void removeHistoryMessage(String key) {
        if (MESSAGE != null) {
            MESSAGE.remove(key);
        }
    }

    public static void removeAllHistoryMessage() {
        if (MESSAGE != null) {
            MESSAGE.clear();
            MESSAGE = null;
        }
    }
}

