package com.example.musicplayer.controll_alarm;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.AlarmAdapter;
import com.example.musicplayer.broadcast.AlarmReceiver;
import com.example.musicplayer.database.Database;
import com.example.musicplayer.model.Alarms;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link alarm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class alarm extends Fragment implements AlarmAdapter.MyAlarmListener{



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public alarm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment alarm.
     */
    // TODO: Rename and change types and number of parameters
    public static alarm newInstance(String param1, String param2) {
        alarm fragment = new alarm();
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
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        init(view);
        setHasOptionsMenu(true);
        return view;
    }

    RecyclerView rv;
    Database database; // thao tac voi database
    ArrayList<Alarms> arrayAlarms;
    AlarmAdapter mAdapter;

    private void init(View view) { // khoi tao
        rv = view.findViewById(R.id.recyclerView);
        LinearLayoutManager lm = new LinearLayoutManager(this.getActivity().getApplicationContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(lm);
        rv.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));

        arrayAlarms = new ArrayList<>();
        setAdapter();

        // tao database
        database = new Database((NoteActivity) alarm.this.getActivity(), "note.sql", null, 1);
        // tao bang
        database.QueryData("CREATE TABLE IF NOT EXISTS Alarms(Id INTEGER PRIMARY KEY AUTOINCREMENT, Alarm VARCHAR(200), Note VARCHAR(200), CheckAlarm INTEGER)");
        // them data
        GetDataAlarm();
    }

    public void setAdapter() {
        mAdapter = new AlarmAdapter(arrayAlarms, this.getActivity());
        mAdapter.setMyAlarmListener(this);
        rv.setAdapter(mAdapter);
    }

    public void GetDataAlarm(){ // lay data
        Cursor dataAlarm = database.GetData("SELECT * FROM Alarms");
        arrayAlarms.clear();
        while(dataAlarm.moveToNext()){
            String alarm = dataAlarm.getString(1);
            String note = dataAlarm.getString(2);
            int check = dataAlarm.getInt(3);
            int id = dataAlarm.getInt(0);
            arrayAlarms.add(new Alarms(id, alarm, note,check));

        }
        mAdapter.notifyDataSetChanged(); // tra ve ham getView
    }

    @Override
    public void update(int pos) { // cap nhap
        Alarms a = arrayAlarms.get(pos);

        Intent in = new Intent(alarm.this.getActivity(), Edit_Alarm.class);
        in.putExtra("alarm", a);
        startActivity(in);
    }

    @Override
    public void remove(int pos) { // xoa
        database.QueryData("DELETE FROM Alarms WHERE Id = '"+ arrayAlarms.get(pos).getIdAlarm() + "'");
        Toast.makeText(alarm.this.getActivity(), "Xóa thành công", Toast.LENGTH_SHORT).show();
        setAdapter();
        GetDataAlarm();
    }

    @Override
    public void checked(int pos) {
        if(arrayAlarms.get(pos).getCheck() == 1) {
            database.QueryData("UPDATE Alarms SET CheckAlarm = " + 0 + " WHERE Id = '" + arrayAlarms.get(pos).getIdAlarm() + "' ");
            AlarmSetting(pos,true);
        }
        else {
            database.QueryData("UPDATE Alarms SET CheckAlarm = " + 1 + " WHERE Id = '" + arrayAlarms.get(pos).getIdAlarm() + "' ");
            AlarmSetting(pos, false);
        }

        Toast.makeText(alarm.this.getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        setAdapter();
        GetDataAlarm();
    }

    @Override
    public void AlarmSetting(int pos, boolean canc) {

        SharedPreferences share = getActivity().getSharedPreferences("alarm_Loop",MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();

        String time = arrayAlarms.get(pos).getAlarm();
        String[] arr = time.split(":");

        Calendar now = Calendar.getInstance();
        Calendar calendar = (Calendar) now.clone();

        // calendar is called to get current time in hour and minute
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(arr[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);

        if(calendar.compareTo(now) < 0) { // thoi gian alarm = now
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        editor.putInt("id_now", arrayAlarms.get(pos).getIdAlarm()); // id hien tai
        editor.apply();
        editor.putString("note",arrayAlarms.get(pos).getNote()); // ghi chu
        editor.apply();
        if(canc)
            alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.add_cong_viec, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuAdd){
            Intent in = new Intent(getActivity(), CreateNewAlarm.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}