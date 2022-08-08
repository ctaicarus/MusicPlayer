package com.example.musicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Alarms;

import java.util.ArrayList;

public class AlarmAdapter extends RecyclerView.Adapter<MyViewHolder2> {
    ArrayList<Alarms> listAlarms;
    Context mContext;


    public void setListAlarms(ArrayList<Alarms> listAlarms) {
        this.listAlarms = listAlarms;
    }

    public interface MyAlarmListener{ // interface lang nghe xu kien
        void remove(int pos); // xoa
        void update(int pos);
        void checked(int pos);
        void AlarmSetting(int pos, boolean canc);
    }

    private MyAlarmListener listener; // khai bao interface

    public void setMyAlarmListener(MyAlarmListener l){
        this.listener = l;
    } // set gia tri interface

    public AlarmAdapter(ArrayList<Alarms> lists, Context context) {
        this.listAlarms = lists;
        this.mContext = context;
    }

    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.dong_cong_viec, viewGroup, false);
        return new MyViewHolder2(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 myViewHolder, final int i) {
        int pos = i;
        Alarms myAlarm = listAlarms.get(i);
        myViewHolder.txtAlarm.setText(myAlarm.getAlarm());
        myViewHolder.txtNote.setText(myAlarm.getNote());

        if(myAlarm.getCheck() == 1) {
            myViewHolder.btnSwitch.setChecked(true);
            listener.AlarmSetting(pos, false);
        }

        else {
            myViewHolder.btnSwitch.setChecked(false);
        }


        myViewHolder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.remove(pos);
                }
            }
        });

        myViewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.update(pos);
                }
            }
        });

        myViewHolder.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.checked(pos);
                    Log.d("ad", "4");
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return listAlarms.size();
    }
}

class MyViewHolder2 extends RecyclerView.ViewHolder {
    TextView txtAlarm;
    TextView txtNote;
    Switch btnSwitch;
    Button btnDel;
    Button btnEdit;

    public MyViewHolder2(@NonNull View itemView) {
        super(itemView);
        txtAlarm = itemView.findViewById(R.id.namework);
        txtNote = itemView.findViewById(R.id.tvNote);
        btnSwitch = itemView.findViewById(R.id.btnSwitch);
        btnEdit = itemView.findViewById(R.id.edit);
        btnDel = itemView.findViewById(R.id.delete);
    }

}
