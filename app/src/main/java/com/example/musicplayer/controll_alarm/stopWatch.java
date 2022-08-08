package com.example.musicplayer.controll_alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.MainActivity;
import com.example.musicplayer.broadcast.MyBroadcastReceiver;
import com.example.musicplayer.service.RingtonePlayingService;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link stopWatch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class stopWatch extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public stopWatch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment stopWatch.
     */
    // TODO: Rename and change types and number of parameters
    public static stopWatch newInstance(String param1, String param2) {
        stopWatch fragment = new stopWatch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_stop_watch, container, false);
        init(view);
        return view;
    }

    MyBroadcastReceiver bcr;
    Button btnStart, btnEdit, btnDel;
    TextView stopWatchTV;
    EditText hh, mm, ss;
    Thread thread;
    long startTime;
    volatile boolean flag = false;
    static MediaPlayer mediaPlayer;

    private void init(View view) {
        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnDel = view.findViewById(R.id.btnDel);
        stopWatchTV = view.findViewById(R.id.stopWatchTV);
        hh = view.findViewById(R.id.edHour);
        mm = view.findViewById(R.id.edMin);
        ss = view.findViewById(R.id.edSec);

        btnStart.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDel.setOnClickListener(this);
    }

    private void StartTimer() {
        int hour, min, sec;

        if(hh.getText().toString().length() == 0)
            hour = 0;
        else
            hour = Integer.parseInt(hh.getText().toString());

        if(mm.getText().toString().length() == 0)
            min = 0;
        else
            min = Integer.parseInt(mm.getText().toString());

        if(ss.getText().toString().length() == 0)
            sec = 0;
        else
            sec = Integer.parseInt(ss.getText().toString());

        startTime = (((hour * 60) + min) * 60 + sec) * 1000;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(long i = 0; i <= startTime; i+= 1000) {

                    if(flag) {
                        if(handler.hasMessages(1))
                            handler.removeMessages(1);

                        break;
                    }

                    String getTimer = ConvertTime(startTime, i);

                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = getTimer;
                    handler.sendMessage(msg);

                    if(i == startTime) {
                        ShowNoti();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private String ConvertTime(long startTime, long count) {
        long times = startTime - count;
        String sec = String.valueOf((times / 1000) % 60);
        String min = String.valueOf((times / (60* 1000)) % 60);
        String hour = String.valueOf(times / (3600* 1000));

        if(hour.length() == 1)
            hour = "0" + hour;
        if(min.length() == 1)
            min = "0" + min;
        if(sec.length() == 1)
            sec = "0" + sec;

        return hour + ":" + min + ":" + sec;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String timer = (String) msg.obj;
            stopWatchTV.setText(timer);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnDel.setEnabled(true);
                btnDel.setVisibility(View.VISIBLE);

                btnEdit.setVisibility(View.VISIBLE);
                btnEdit.setEnabled(true);
                hh.setEnabled(false);
                mm.setEnabled(false);
                ss.setEnabled(false);
                flag = false;
                StartTimer();
                break;

            case R.id.btnDel:
                btnStart.setEnabled(true);
                btnStart.setVisibility(View.VISIBLE);

                btnDel.setEnabled(false);
                btnDel.setVisibility(View.INVISIBLE);

                btnEdit.setVisibility(View.INVISIBLE);
                btnEdit.setEnabled(false);

                flag = true;
                stopWatchTV.setText("");
                hh.setText("");
                mm.setText("");
                ss.setText("");
                hh.setEnabled(true);
                mm.setEnabled(true);
                ss.setEnabled(true);
                break;

            case R.id.btnEdit:
                btnStart.setEnabled(true);
                btnStart.setVisibility(View.VISIBLE);

                btnDel.setEnabled(true);
                btnDel.setVisibility(View.VISIBLE);

                btnEdit.setVisibility(View.VISIBLE);
                btnEdit.setEnabled(true);

                hh.setEnabled(true);
                mm.setEnabled(true);
                ss.setEnabled(true);
                break;
        }
    }

    private void ShowNoti() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity())
                .setContentTitle("Time up!!!")
                .setSmallIcon(R.drawable.mic)
                .setShowWhen(true)
                .setContentText("Draw to close")
                .setAutoCancel(true);
        NotificationChannel channel = new NotificationChannel("Channel_ID", "alarm", NotificationManager.IMPORTANCE_HIGH);
        builder.setChannelId("Channel_ID");

        Notification noti = builder.build();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(100, noti);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.alert);
        mediaPlayer.start();

        try {
            Thread.sleep(6000);
            mediaPlayer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.backtomain, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

}