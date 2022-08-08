package com.example.musicplayer.controll_alarm;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link globalTime#newInstance} factory method to
 * create an instance of this fragment.
 */
public class globalTime extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public globalTime() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment globalTime.
     */
    // TODO: Rename and change types and number of parameters
    public static globalTime newInstance(String param1, String param2) {
        globalTime fragment = new globalTime();
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
        View view = inflater.inflate(R.layout.fragment_global_time, container, false);
        init(view);
        return view;
    }
    TextView time;

    private void init(View view) {
        time = view.findViewById(R.id.gltime);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Calendar now = Calendar.getInstance();
                    Date time = now.getTime();
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = df.format(time);
                    handler.sendMessage(msg);

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


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.add_cong_viec, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuAdd){
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            View mView = getLayoutInflater().inflate(R.layout.dialog_select_city,null);

            Spinner spinner = mView.findViewById(R.id.spinnerCity);
            String[] citys = {"Bắc Kinh","London","New York", "Paris", "Tokyo"};

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, citys){

                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    TextView tv = ((TextView) v);
                    tv.setTextSize(24);
                    return v;
                }
            };;

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);

            Button btnNo = (Button)mView.findViewById(R.id.btnNo);
            Button btnYes = (Button)mView.findViewById(R.id.btnYes);
            alert.setView(mView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"No access Internet issues", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String gltime = (String) msg.obj;
            time.setText(gltime);
        }
    };

    private void setView(TextView city, TextView note, TextView gltimes, String cityName, String Note, String time) {
        city.setText(cityName);
        note.setText(Note);
        gltimes.setText(time);
    }
}