package com.example.musicplayer.service;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.MathActivity;

public class RingtonePlayingService extends Service  {
    public static String CHANNEL_ID = "id_channel";
    Context mcontect = RingtonePlayingService.this;
    static MediaPlayer mediaPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences share = getSharedPreferences("alarm_Loop",MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= 26) {
            int id = share.getInt("id_now", 0); // id alarm hien tai
            String note = share.getString("note", "Báo thức"); // lay ra note hien tai
            String song = share.getString("song" + id, "baby"); // bai hat hien tai

            intent = new Intent(getApplicationContext(), MathActivity.class);
            PendingIntent activityPending = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);;

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_wakeup); // custom noti
            remoteViews.setTextViewText(R.id.note_noti_wakeup, note);

            remoteViews.setOnClickPendingIntent(R.id.btn_go_to_math, activityPending); // bat su kien chuyen man hinh giai toan

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "notifi", NotificationManager.IMPORTANCE_DEFAULT); // dinh nghia 1 channel
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontect); // giao dien thong bao
            builder.setContent(remoteViews);
            builder.setSmallIcon(R.drawable.ic_audio);
            builder.setContentText("Bao thuc");
            builder.setChannelId(CHANNEL_ID);

            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            //notificationManager.notify(100, notification); // show thong bao

            if(song.equals("Mai xa")){
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.maixa);
                mediaPlayer.start();
            } else if (song.equals("Cu chill thoi")){
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.cuchillthoi);
                mediaPlayer.start();
            } else if(song.equals("Nang am xa dan")){
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.nangamxadan);
                mediaPlayer.start();
            } else{
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.emcuangayhq);
                mediaPlayer.start();
            }
            startForeground(FOREGROUND_SERVICE_TYPE_LOCATION, notification); // show notification
            Log.d("tag", "onCreate:1 ");
        } else {
            int id = share.getInt("id_now", 0); // id alarm hien tai
            String note = share.getString("note", "Báo thức"); // lay ra note hien tai
            String song = share.getString("song" + id, "baby"); // bai hat hien tai

            intent = new Intent(getApplicationContext(), MathActivity.class);
            PendingIntent activityPending = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);;

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_wakeup); // custom noti
            remoteViews.setTextViewText(R.id.note_noti_wakeup, note);

            remoteViews.setOnClickPendingIntent(R.id.btn_go_to_math, activityPending); // bat su kien chuyen man hinh giai toan

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "notifi", NotificationManager.IMPORTANCE_DEFAULT); // dinh nghia 1 channel
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontect); // giao dien thong bao
            builder.setContent(remoteViews);
            builder.setSmallIcon(R.drawable.ic_audio);
            builder.setContentText("Bao thuc");
            builder.setChannelId(CHANNEL_ID);

            Notification notification = builder.build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(100, notification); // show thong bao

            if(song.equals("Mai xa")){
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.maixa);
                mediaPlayer.start();
            } else if (song.equals("Cu chill thoi")){
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.cuchillthoi);
                mediaPlayer.start();
            } else if(song.equals("Nang am xa dan")){
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.nangamxadan);
                mediaPlayer.start();
            } else{
                mediaPlayer = MediaPlayer.create(RingtonePlayingService.this, R.raw.emcuangayhq);
                mediaPlayer.start();
            }
            Log.d("tag", "onCreate:2 ");
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

}
