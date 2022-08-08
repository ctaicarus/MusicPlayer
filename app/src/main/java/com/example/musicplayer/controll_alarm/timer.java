package com.example.musicplayer.controll_alarm;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;
import com.example.musicplayer.activity.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link timer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class timer extends Fragment implements  View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public timer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment timer.
     */
    // TODO: Rename and change types and number of parameters
    public static timer newInstance(String param1, String param2) {
        timer fragment = new timer();
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
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        init(view);
        return view;
    }

    Button btnStart, btnStop, btnLoop, btnReset;
    TextView timerTV, loop1, loop2, loop3, loop4;
    Thread thread;
    long startTime, sysTime, updateTime;
    volatile boolean flag = false, loop = false;
    long count, t1, t2, t3;

    private void init(View view) {
        btnStart = (Button) view.findViewById(R.id.btnStart);
        btnStop = view.findViewById(R.id.btnStop);
        btnLoop = view.findViewById(R.id.btnLoop);
        btnReset = view.findViewById(R.id.btnReset);
        timerTV = view.findViewById(R.id.TimerTV);
        loop1 = view.findViewById(R.id.loop1);
        loop2 = view.findViewById(R.id.loop2);
        loop3 = view.findViewById(R.id.loop3);
        loop4 = view.findViewById(R.id.loop4);

        count = 0;

        btnStart.setOnClickListener(this);
        btnLoop.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    private void StartTimer() {
        String st = timerTV.getText().toString();
        String[] arr = st.split(":");

        Calendar start = Calendar.getInstance();

        startTime = ((Integer.valueOf(arr[0]) * 60) + Integer.valueOf(arr[1])) * 60 * 1000;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0;;i++) {
                    if(flag) {
                        SharedPreferences share = getActivity().getSharedPreferences("timer",MODE_PRIVATE);
                        SharedPreferences.Editor editor = share.edit();

                        editor.putString("timer",timerTV.getText().toString());
                        editor.apply();

                        if(handler.hasMessages(1))
                            handler.removeMessages(1);


                        break;
                    }

                    Calendar now = Calendar.getInstance();

                    sysTime = now.getTimeInMillis() - start.getTimeInMillis();
                    updateTime = startTime + sysTime;

                    if(loop) {
                        loop = false;
                        count++;
                        if(count == 1) {
                            t1 = updateTime;
                            loop1.setText("Vòng 1 \t\t\t\t" + timerTV.getText().toString());
                        }

                        if(count == 2) {
                            t2 = updateTime;
                            String l2 = ConvertTime(t2 - t1);
                            loop2.setText("Vòng 2 \t\t\t\t" + l2);
                        }

                        if(count == 3) {
                            t3 = updateTime;
                            String l3 = ConvertTime(t3 - t2);
                            loop3.setText("Vòng 3 \t\t\t\t" + l3);
                        }
                        if(count == 4){
                            String l4 = ConvertTime(updateTime - t3);
                            loop4.setText("Vòng 4 \t\t\t\t" + l4);
                        }
                    }

                    String getTimer = ConvertTime(updateTime);
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = getTimer;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private String ConvertTime(long updateTime) {
        String sec = String.valueOf((updateTime / 1000) % 60);
        String min = String.valueOf((updateTime / (60* 1000)) % 60);
        String hour = String.valueOf(updateTime / (3600* 1000));
        long mil =  ((Integer.parseInt(hour) * 60) + Integer.parseInt(min)) * 60 * 1000;
        String millis = String.valueOf((updateTime - mil) % 100);

        if(hour.length() == 1)
            hour = "0" + hour;
        if(min.length() == 1)
            min = "0" + min;
        if(sec.length() == 1)
            sec = "0" + sec;
        if(millis.length() == 1)
            millis = "0" + millis;

        return hour + ":" + min + ":" + sec + ":" + millis;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String timer = (String) msg.obj;
            timerTV.setText(timer);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                btnStart.setEnabled(false);
                btnStart.setVisibility(View.INVISIBLE);

                btnLoop.setEnabled(true);
                btnLoop.setVisibility(View.VISIBLE);

                btnStop.setVisibility(View.VISIBLE);
                btnStop.setEnabled(true);

                btnReset.setVisibility(View.INVISIBLE);
                btnReset.setEnabled(false);

                if(!timerTV.getText().toString().equals("00:00:00:00")) {
                    SharedPreferences share = getActivity().getSharedPreferences("timer",MODE_PRIVATE);
                    String times = share.getString("timer", "00:00:00:00");
                    timerTV.setText(times);
                }

                flag = false;
                StartTimer();
                break;

            case R.id.btnStop:
                btnStart.setEnabled(true);
                btnStart.setVisibility(View.VISIBLE);

                btnReset.setEnabled(true);
                btnReset.setVisibility(View.VISIBLE);

                btnLoop.setEnabled(false);
                btnLoop.setVisibility(View.INVISIBLE);

                btnStop.setVisibility(View.INVISIBLE);
                btnStop.setEnabled(false);

                flag = true;
                break;

            case R.id.btnReset:
                btnReset.setEnabled(false);
                btnReset.setVisibility(View.INVISIBLE);

                btnLoop.setVisibility(View.VISIBLE);
                timerTV.setText("00:00:00:00");
                count = 0;
                loop1.setText("");
                loop2.setText("");
                loop3.setText("");
                loop4.setText("");
                break;

            case R.id.btnLoop:
                loop = true;
                break;
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