package com.roma.android.sihmi.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.roma.android.sihmi.core.CoreApplication;
import com.roma.android.sihmi.helper.NotificationHelper;
import com.roma.android.sihmi.model.database.entity.Contact;
import com.roma.android.sihmi.model.database.entity.notification.Token;
import com.roma.android.sihmi.model.database.interfaceDao.ContactDao;
import com.roma.android.sihmi.model.database.interfaceDao.UserDao;

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

            NotificationHelper.sendNotification(remoteMessage, contact, this);
        } catch (NullPointerException e){
            e.printStackTrace();
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

}
