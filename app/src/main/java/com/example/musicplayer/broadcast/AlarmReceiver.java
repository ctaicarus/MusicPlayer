package com.example.musicplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.musicplayer.service.RingtonePlayingService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("tag", "Alrm1");
            context.startForegroundService(service_intent);
        } else {
            Log.d("tag", "Alrm2");
            context.startService(service_intent);
        }


    }
}
