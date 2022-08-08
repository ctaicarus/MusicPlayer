package com.example.musicplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer.model.MyMath;
import com.example.musicplayer.controll_alarm.NoteActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.service.RingtonePlayingService;

public class MathActivity extends AppCompatActivity {

    MyMath myMath;
    Button closeButton;
    TextView showingExpressionTV;
    EditText enteringResultET;
    Context mcontext = MathActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_math);

        closeButton=findViewById(R.id.closeButton);
        showingExpressionTV=findViewById(R.id.showingExpressionTV);
        enteringResultET=findViewById(R.id.enteringResultET);
        enteringResultET.setEnabled(false);
        closeButton.setText("Bài toán");


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMath=MyMath.autoGenerateExpressionTwoNumber(50,100,"+-");
                showingExpressionTV.setText(myMath.toString());
                enteringResultET.setHint("Enter your result");
                enteringResultET.setEnabled(true);
            }
        });


        enteringResultET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int result=(int)myMath.getResult();
                if(s.toString().equals(result+"")){ // giai dung
                    Toast.makeText(MathActivity.this, "Have turned off", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(MathActivity.this, RingtonePlayingService.class);
                    mcontext.stopService(intent1);
                    enteringResultET.setEnabled(false);
                    Intent intent2 = new Intent(MathActivity.this, NoteActivity.class);
                    startActivity(intent2);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

}