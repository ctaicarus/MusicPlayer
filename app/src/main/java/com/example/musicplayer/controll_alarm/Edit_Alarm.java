package com.example.musicplayer.controll_alarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.database.Database;
import com.example.musicplayer.model.Alarms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Edit_Alarm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinnersong;
    private static final String[] songs = {"Mai xa","Cu chill thoi", "Nang am xa dan", "Em cua ngay hom qua"};
    private static final ArrayList<String> listSongs = new ArrayList<>(Arrays.asList(songs));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        TimePicker tp = (TimePicker) findViewById(R.id.tpkTime);
        tp.setIs24HourView(true);

        Intent in = getIntent();
        Alarms a = (Alarms) in.getSerializableExtra("alarm");

        String time = a.getAlarm().toString();
        String[] arr = time.split(":");

        tp.setHour(Integer.parseInt(arr[0]));
        tp.setMinute(Integer.parseInt(arr[1]));

        EditText note = findViewById(R.id.edNote);
        note.setText(a.getNote().toString());

        SharedPreferences sharedPreferences = getSharedPreferences("alarm_Loop", MODE_PRIVATE);
        String alarmLoop = sharedPreferences.getString("alarm" + a.getIdAlarm(), "");
        String song = sharedPreferences.getString("song" + a.getIdAlarm() +"","");

        int pos = listSongs.indexOf(song);

        spinnersong = findViewById(R.id.spinnerSong);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Edit_Alarm.this,
                android.R.layout.simple_list_item_checked,songs);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnersong.setAdapter(adapter2);
        spinnersong.setOnItemSelectedListener(this);
        spinnersong.setSelection(pos);

        CheckedCb(alarmLoop);

        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hour = String.valueOf(tp.getHour());
                String minute = String.valueOf(tp.getMinute());
                if(minute.length() == 1)
                    minute = "0" + minute;

                Intent in = new Intent(Edit_Alarm.this, NoteActivity.class);
                String str = note.getText().toString();
                if(str.length() <= 0){
                    Toast.makeText(Edit_Alarm.this, "Nhập ghi chú", Toast.LENGTH_SHORT ).show();
                } else {
                    Database database = new Database(Edit_Alarm.this, "note.sql", null, 1);
                    database.QueryData("UPDATE Alarms SET Alarm = '" + hour + ":" + minute + "', Note = '"+ str + "', CheckAlarm = " + 1 + " WHERE Id = '" + a.getIdAlarm() + "' ");
                    startActivity(in);
                    Toast.makeText(Edit_Alarm.this, "Cập nhật thành công", Toast.LENGTH_SHORT ).show();
                }
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void CheckedCb(String s) {
        CheckBox Mon, Tue, Wed, Thu, Fri, Sat, Sun;

        Mon = findViewById(R.id.Mon);
        Tue = findViewById(R.id.Tue);
        Wed = findViewById(R.id.Wed);
        Thu = findViewById(R.id.Thu);
        Fri = findViewById(R.id.Fri);
        Sat = findViewById(R.id.Sat);
        Sun = findViewById(R.id.Sun);

        String[] Loop = s.split(" ");

        for(int i = 0; i < Loop.length; i++) {
            if(Loop[i].equals("Mon")) Mon.setChecked(true);
            else if(Loop[i].equals("Tue")) Tue.setChecked(true);
            else if(Loop[i].equals("Wed")) Wed.setChecked(true);
            else if(Loop[i].equals("Thu")) Thu.setChecked(true);
            else if(Loop[i].equals("Fri")) Fri.setChecked(true);
            else if(Loop[i].equals("Sat")) Sat.setChecked(true);
            else if(Loop[i].equals("Sun")) Sun.setChecked(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}