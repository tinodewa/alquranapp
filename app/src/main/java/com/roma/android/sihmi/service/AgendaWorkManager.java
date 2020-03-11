package com.roma.android.sihmi.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.roma.android.sihmi.R;
import com.roma.android.sihmi.view.activity.MainActivity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AgendaWorkManager extends Worker {
    Context context;

    public AgendaWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    public static void scheduleReminder(long duration, Data data, String tag) {
        Log.d("hallloo", "scheduleReminder: "+duration);
        OneTimeWorkRequest notificationWork;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            Constraints constraints = new Constraints.Builder()
//                    .setRequiresDeviceIdle(true)
//                    .setRequiresCharging(true)
//                    .build();

             notificationWork = new OneTimeWorkRequest.Builder(AgendaWorkManager.class)
                    .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                    .addTag(tag)
//                     .setConstraints(constraints)
                    .setInputData(data)
                    .build();
        } else {
            notificationWork = new OneTimeWorkRequest.Builder(AgendaWorkManager.class)
                    .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                    .addTag(tag)
                    .setInputData(data)
                    .build();
        }


        WorkManager instance = WorkManager.getInstance();
        instance.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, notificationWork);
    }


    public static void cancelReminder(String tag) {
        WorkManager instance = WorkManager.getInstance();
        instance.cancelAllWorkByTag(tag);
    }

    public static void cancelAllReminder() {
        WorkManager instance = WorkManager.getInstance();
        instance.cancelAllWork();
    }

    @NonNull
    @Override
    public Result doWork() {
        //trigger Notification
        String title = getInputData().getString("judul");
        String text = getInputData().getString("textt");
        int id = getInputData().getInt("id", 0);
        Log.d("hallloo", "doWork: "+title+" - "+text);
        sendNotification(title, text, id);
        return Result.success();
    }

    private void sendNotification(String title, String text, int id) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_notifications))
                .setSmallIcon(R.drawable.ic_notifications)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(defaultSound)
                .setAutoCancel(true);
        Objects.requireNonNull(notificationManager).notify(id, notification.build());
    }
}
