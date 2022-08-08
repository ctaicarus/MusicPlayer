package com.example.musicplayer.controll_alarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.R;
import com.example.musicplayer.database.Database;

public class CreateNewAlarm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int pos = 0;
    private Spinner spinnersong;
    private static final String[] song = {"Mai xa","Cu chill thoi", "Nang am xa dan","Em cua ngay hom qua"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);


        spinnersong = findViewById(R.id.spinnerSong);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CreateNewAlarm.this,
                android.R.layout.simple_list_item_checked,song);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinnersong.setAdapter(adapter2);
        spinnersong.setOnItemSelectedListener(this);

        TimePicker tp = (TimePicker) findViewById(R.id.tpkTime);
        tp.setIs24HourView(true);

        findViewById(R.id.buttonThem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hour = String.valueOf(tp.getHour());
                String minute = String.valueOf(tp.getMinute());
                if(minute.length() == 1)
                    minute = "0" + minute;

                EditText note = findViewById(R.id.edNote);
                String time = hour + ":" + minute;

                Intent in = new Intent(CreateNewAlarm.this, NoteActivity.class);
                if(note.equals("")){
                    Toast.makeText(CreateNewAlarm.this, "Nhập ghi chú", Toast.LENGTH_SHORT ).show();
                } else {
                    Database database = new Database(CreateNewAlarm.this, "note.sql", null, 1);
                    database.QueryData("INSERT INTO Alarms VALUES(null, '"+ time +"','"+ note.getText().toString() +"', " + 1 + ")");

                    String alarmLoop = CheckedCb();
                    Cursor dataAlarm = database.GetData("SELECT MAX(Id) FROM Alarms");
                    int id = -1;
                    while(dataAlarm.moveToNext()){
                        id = dataAlarm.getInt(0);
                        break;
                    }

                    SharedPreferences share = getSharedPreferences("alarm_Loop",MODE_PRIVATE);
                    SharedPreferences.Editor editor = share.edit();
                    editor.putString("alarm" + id, alarmLoop);
                    editor.putString("song" + id, song[pos]);
                    Log.d("tag", id + "");
                    Log.d("tag", song[pos] + "");
                    editor.apply();

                    startActivity(in);
                    Toast.makeText(CreateNewAlarm.this, "Thêm thành công", Toast.LENGTH_SHORT ).show();
                }

            }
        });

        findViewById(R.id.buttonHuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String CheckedCb() {
        CheckBox Mon, Tue, Wed, Thu, Fri, Sat, Sun;

        Mon = findViewById(R.id.Mon);
        Tue = findViewById(R.id.Tue);
        Wed = findViewById(R.id.Wed);
        Thu = findViewById(R.id.Thu);
        Fri = findViewById(R.id.Fri);
        Sat = findViewById(R.id.Sat);
        Sun = findViewById(R.id.Sun);

        String str = "";
        str = Mon.isChecked() ? str + "Mon " : str;
        str = Tue.isChecked() ? str + "Tue " : str;
        str = Wed.isChecked() ? str + "Wed " : str;
        str = Thu.isChecked() ? str + "Thu " : str;
        str = Fri.isChecked() ? str + "Fri " : str;
        str = Sat.isChecked() ? str + "Sat " : str;
        str = Sun.isChecked() ? str + "Sun " : str;

        return str;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                pos = 0;
                // Whatever you want to happen when the first item gets selected
                break;
            case 1:
                pos = 1;
                // Whatever you want to happen when the second item gets selected
                break;
            case 2:
                pos = 2;
                // Whatever you want to happen when the thrid item gets selected
                break;
            case 3:
                pos = 3;
                // Whatever you want to happen when the thrid item gets selected
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}