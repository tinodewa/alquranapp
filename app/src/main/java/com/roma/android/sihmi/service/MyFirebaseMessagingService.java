package com.roma.android.sihmi.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.roma.android.sihmi.R;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.notification.OreoNotification;
import com.roma.android.sihmi.model.database.entity.notification.Token;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;
import com.roma.android.sihmi.utils.Tools;
import com.roma.android.sihmi.view.activity.ChatActivity;
import com.roma.android.sihmi.view.activity.ProfileActivity;

import static io.fabric.sdk.android.Fabric.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    UserDao userDao;
    ContactDao contactDao;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            Log.d(TAG, "From: MessageDariFirebasenyaIni " + remoteMessage.getFrom() + " - " + remoteMessage.getData().get("title") + " - " + remoteMessage.getData().get("body"));

            userDao = CoreApplication.get().getConstant().getUserDao();
            contactDao = CoreApplication.get().getConstant().getContactDao();

            String sented = remoteMessage.getData().get("sented");
            String user = remoteMessage.getData().get("user");

            SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            String currentUser = preferences.getString("currentuser", "none");


            Log.d(TAG, "onMessageReceived: Message " + sented + " - " + user + " - " + currentUser + " - " + userDao.getUser().get_id());
            Contact contact = contactDao.getContactById(user);

            if (!userDao.getUser().get_id().equals(user) && userDao.getUser().get_id().equals(sented) && !contact.isBisukan()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }
        } catch (NullPointerException e){

        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(s);
        reference.child(userDao.getUser().get_id()).setValue(token);
        Log.d(TAG, "onNewToken: Message "+s);
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        Log.d(TAG, "sendNotification: OREI Message");
        int idNotif;
        Intent intent;

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");


        Contact contact = contactDao.getContactById(user);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (title.equals("New Message")){
            idNotif = 1;
            intent = new Intent(this, ChatActivity.class)
                    .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id());
        } else {
            idNotif = 2;
            intent = new Intent(this, ProfileActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, idNotif, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        oreoNotification.getManager().notify(idNotif, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "sendNotification: Message");
        int idNotif;
        Intent intent;

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Contact contact = contactDao.getContactById(user);
        Log.d(TAG, "sendNotification: "+contact.getFullName()+" - "+user);
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        if (title.equals("New Message")){
            idNotif = 1;
            intent = new Intent(this, ChatActivity.class)
                    .putExtra("nama", contact.getFullName()).putExtra("iduser", contact.get_id());
        } else {
            idNotif = 2;
            intent = new Intent(this, ProfileActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, idNotif, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorlogo))
                .setContentTitle(title)
                .setContentText(Tools.convertUTF8ToString(body))
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        noti.notify(idNotif, builder.build());
    }

}
