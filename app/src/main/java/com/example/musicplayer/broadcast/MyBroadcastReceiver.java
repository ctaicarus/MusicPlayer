package com.example.musicplayer.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    public interface MyBroadcastLis {
        void pause();
        void next();
        void pre();
    }

    MyBroadcastLis myBroadcastLis;
    public void setMyBroadcastLis(MyBroadcastLis l){
        myBroadcastLis = l;
    }
    @Override
    public void onReceive(Context context, Intent intent) { // nhan broadcast
        String action =  intent.getAction();
        switch (action){
            case "comm.cta.notificationMusicPlayerPause":
                if(myBroadcastLis!=null) myBroadcastLis.pause();
                break;
            case "comm.cta.notificationMusicPlayerNext":
                if(myBroadcastLis!=null) myBroadcastLis.next();
                break;
            case "comm.cta.notificationMusicPlayerPre":
                if(myBroadcastLis!=null) myBroadcastLis.pre();
                break;
        }

    }
}