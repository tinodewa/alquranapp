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
import com.roma.android.sihmi.view.activity.ChatGroupActivity;
import com.roma.android.sihmi.view.activity.MainActivity;
import com.roma.android.sihmi.view.activity.SplashActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {
    private static HashMap<String, List<Message>> MESSAGE = new HashMap<>();
    private static List<Integer> UNUSED_MESSAGE_RC = new ArrayList<>();
    private static List<Integer> USED_MESSAGE_RC = new ArrayList<>();
    private static HashMap<String, Integer> MESSAGE_RC = new HashMap<>();
    private static String CHANNEL_ID = "com.example.root.sihmi";
    public static final int ID_CHAT = 1;
    private static final int ID_GENERAL = 2;
    private static final String NOTIFICATION_DELETED_ACTION = "NOTIFICATION_DELETED";
    private static final String TYPE_MESSAGE = "__type_message__";
    private static String GROUP_NOTIFICATION_MESSAGE = "com.roma.android.sihmi.MESSAGE";

    public static void sendNotification(RemoteMessage remoteMessage, Contact contact, Context context) {
//        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String groupChat = remoteMessage.getData().get("groupChat");
        String type = remoteMessage.getData().get("type");
        String groupName = null;

        if (groupChat != null && groupChat.equals("GROUP_CHAT")) {
            groupName = remoteMessage.getData().get("sented");
        }

        if (type != null) {
            if (type.equals(TYPE_MESSAGE)) {
                if (!contact.isBisukan() || groupName != null) {
                    Message message = new Message(contact.getNama_panggilan(), (body != null) ? Tools.convertUTF8ToString(body) : "", System.currentTimeMillis());
                    if (groupName != null) {
                        message.setGroupName(groupName);
                    }
                    sendNotificationMessaging(message, context, contact, groupName);
                }
            }
        }
    }

    private static void sendNotificationMessaging(Message message, Context context, Contact contact, String groupName) {
        int SUMMARY_ID = 0;
        boolean isGroup = groupName != null;
        String key;
        if (isGroup) {
            key = groupName+"_"+ID_CHAT;
        }
        else {
            key = contact.get_id()+"_"+ID_CHAT;
        }

        List<Message> messages = MESSAGE.get(key);

        if (messages == null) {
            messages = new ArrayList<>();
        }

        Integer messageRequestCode = MESSAGE_RC.get(key);
        if (messageRequestCode == null) {
            if (UNUSED_MESSAGE_RC.size() > 0) {
                messageRequestCode = UNUSED_MESSAGE_RC.get(0);
                USED_MESSAGE_RC.add(messageRequestCode);
            }
            else {
                messageRequestCode = USED_MESSAGE_RC.size();
                USED_MESSAGE_RC.add(messageRequestCode);
            }
            Collections.sort(USED_MESSAGE_RC);
        }

        Intent intent;
        if (isGroup) {
            intent = new Intent(context, ChatGroupActivity.class)
                    .putExtra(ChatGroupActivity.NAMA_GROUP, groupName);
        }
        else {
            intent = new Intent(context, ChatActivity.class)
                    .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id());
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, messageRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        messages.add(message);
        MESSAGE.put(key, messages);

        Notification notification;

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("REMOVE MESSAGE", "REMOVE MESSAGE " + key);
                removeHistoryMessage(key);
                Integer messageRc = MESSAGE_RC.get(key);
                if (messageRc != null) {
                    MESSAGE_RC.remove(key);
                    USED_MESSAGE_RC.remove((int) messageRc);
                    UNUSED_MESSAGE_RC.add(messageRc);

                    Collections.sort(USED_MESSAGE_RC);
                    Collections.sort(UNUSED_MESSAGE_RC);
                }
                context.unregisterReceiver(this);
            }
        };

        Intent cancelIntent = new Intent(NOTIFICATION_DELETED_ACTION);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        context.registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER oreo");
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SIHMI", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle(context.getString(R.string.you));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                messagingStyle.setGroupConversation(isGroup);
                messagingStyle.setConversationTitle(groupName);
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
                    .setShowWhen(true)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(cancelPendingIntent)
                    .setAutoCancel(true)
                    .setGroup(GROUP_NOTIFICATION_MESSAGE)
                    .build();
        }
        else {
            Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER not oreo");
            //noinspection deprecation
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(context.getString(R.string.you));
            messagingStyle.setGroupConversation(isGroup);
            messagingStyle.setConversationTitle(groupName);

            for (Message m : messages) {
                //noinspection deprecation
                NotificationCompat.MessagingStyle.Message mMessage = new NotificationCompat.MessagingStyle.Message(m.getText(), m.getTimestamp(), m.getSender());
                messagingStyle.addMessage(mMessage);
            }

            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                    .setStyle(messagingStyle)
                    .setShowWhen(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(cancelPendingIntent)
                    .setAutoCancel(true)
                    .setGroup(GROUP_NOTIFICATION_MESSAGE)
                    .build();
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(getSummaryTitle())
                .setSummaryText(getSummaryTitleShort());

        for (String summary : getMessageSummaryItem()) {
            inboxStyle.addLine(summary);
        }

        Intent mainActivityIntent = new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(getSummaryTitleShort())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setColor(ContextCompat.getColor(context, R.color.colorlogo))
                .setShowWhen(true)
                .setStyle(inboxStyle)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(GROUP_NOTIFICATION_MESSAGE)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setContentIntent(mainActivityPendingIntent)
                .build();

        String tagName;
        if (isGroup) {
            tagName = groupName;
        }
        else {
            tagName = contact.get_id();
        }
        notificationManager.notify(tagName, ID_CHAT, notification);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }

    public static void removeAllMessageRC() {
        if (MESSAGE_RC != null) {
            MESSAGE_RC.clear();
        }

        USED_MESSAGE_RC.clear();
        UNUSED_MESSAGE_RC.clear();
    }

    private static void sendNotificationGeneral(String title, String body, Context context, Contact contact, String tagName) {
        Log.d("NOTIFICATION HELPER", "NOTIFICATION HELPER general");
        Intent intent;
        PendingIntent pendingIntent;
        PendingIntent cancelPendingIntent = null;

        if (contact != null) {
            String key = contact.get_id()+"_"+ID_CHAT;
            body = contact.getNama_panggilan() + " : " + body;

            Integer messageRequestCode = MESSAGE_RC.get(key);
            if (messageRequestCode == null) {
                if (UNUSED_MESSAGE_RC.size() > 0) {
                    messageRequestCode = UNUSED_MESSAGE_RC.get(0);
                    USED_MESSAGE_RC.add(messageRequestCode);
                }
                else {
                    messageRequestCode = USED_MESSAGE_RC.size();
                    USED_MESSAGE_RC.add(messageRequestCode);
                }
                Collections.sort(USED_MESSAGE_RC);
            }

            intent = new Intent(context, ChatActivity.class).putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id());
            pendingIntent = PendingIntent.getActivity(context, messageRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("REMOVE MESSAGE", "REMOVE MESSAGE " + key);
                    removeHistoryMessage(key);
                    Integer messageRc = MESSAGE_RC.get(key);
                    if (messageRc != null) {
                        MESSAGE_RC.remove(key);
                        USED_MESSAGE_RC.remove((int) messageRc);
                        UNUSED_MESSAGE_RC.add(messageRc);

                        Collections.sort(USED_MESSAGE_RC);
                        Collections.sort(UNUSED_MESSAGE_RC);
                    }
                    context.unregisterReceiver(this);
                }
            };

            Intent cancelIntent = new Intent(NOTIFICATION_DELETED_ACTION);
            cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);

            context.registerReceiver(receiver, new IntentFilter(NOTIFICATION_DELETED_ACTION));

        }
        else {
            intent = new Intent(context, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

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
                    .setDeleteIntent(cancelPendingIntent)
                    .setContentIntent(pendingIntent)
                    .setGroup(GROUP_NOTIFICATION_MESSAGE)
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
                    .setDeleteIntent(cancelPendingIntent)
                    .setContentIntent(pendingIntent)
                    .setGroup(GROUP_NOTIFICATION_MESSAGE)
                    .setAutoCancel(true)
                    .build();
        }

        notificationManager.notify(tagName, ID_GENERAL, notification);
    }

    public static void removeHistoryMessage(String key) {
        if (MESSAGE != null) {
            MESSAGE.remove(key);
        }
    }

    public static void removeAllHistoryMessage() {
        if (MESSAGE != null) {
            MESSAGE.clear();
        }
    }

    private static int getCountAllMessage() {
        int count = 0;
        for (String key: MESSAGE.keySet()) {
            List<Message> m = MESSAGE.get(key);
            if (m != null) {
                count += m.size();
            }
        }
        return count;
    }

    private static String getSummaryTitle() {
        int countC = MESSAGE.size();
        int countM = getCountAllMessage();
        String messageName = (countM > 1) ? "messages" : "message";
        String chatName = (countC > 1) ? "chats" : "chat";

        return countM + " new " + messageName + " from " + countC + " " + chatName;
    }

    private static String getSummaryTitleShort() {
        int countC = MESSAGE.size();

        return countC + " new " + (countC > 0 ? "messages" : "message");
    }

    private static List<String> getMessageSummaryItem() {
        List<String> summaries = new ArrayList<>();
        for (String key: MESSAGE.keySet()) {
            List<Message> m = MESSAGE.get(key);
            if (m != null) {
                Message lastMessage = m.get(m.size()-1);
                if (lastMessage.getGroupName() != null) {
                    summaries.add( "(" + lastMessage.getGroupName() + ")" +  lastMessage.getSender() + " " + lastMessage.getText());
                }
                else {
                    summaries.add(lastMessage.getSender() + " " + lastMessage.getText());
                }
            }
        }

        return summaries;
    }
}

