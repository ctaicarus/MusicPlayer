package com.example.musicplayer.controll_alarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NoteActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView nbv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        SharedPreferences sharedPreferences = getSharedPreferences("background", MODE_PRIVATE);
        String background = sharedPreferences.getString("background", "");

        if(background.length() > 0) {
            Drawable d = Drawable.createFromPath(background);
            RelativeLayout rl = findViewById(R.id.alarmClock);
            rl.setBackground(d);
        }

        nbv = findViewById(R.id.navigationView);
        nbv.setOnItemSelectedListener(this);
        nbv.setSelectedItemId(R.id.alarm);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alarm:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new alarm(), "flagment_alarm").commit();
                return true;

            case R.id.globalTime:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new globalTime(), "flagment_global_timer").commit();
                return true;

            case R.id.timer:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new timer(), "flagment_timer").commit();
                return true;

            case R.id.stopWatch:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new stopWatch(), "flagment_stop_watch").commit();
                return true;
        }
        return false;
    }
}