package com.example.musicplayer.activity;


import static com.example.musicplayer.service.RingtonePlayingService.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.adapter.SongListener;
import com.example.musicplayer.broadcast.MyBroadcastReceiver;
import com.example.musicplayer.adapter.MyRecyclerViewAdapter;
import com.example.musicplayer.controll_alarm.NoteActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.MysongListener, MyBroadcastReceiver.MyBroadcastLis, SongListener {
    TextView txtTitle, txtTimeSong, txtTimeTotal;
    SeekBar skSong;
    Button btnPrev, btnPlay, btnStop, btnNext, btnLoop;
    ArrayList<Song> arraySong; // danh sach bai hat
    static MyRecyclerViewAdapter adapter ; // adapter hien thi danh sach bai
    int position = 0; // vi tri phat nhac
    ImageView hinh;
    static MediaPlayer mediaPlayer;
    RecyclerView recyclerView; // recycler hien thi danh sach bai hat
    Animation animation; // hieu ung quay tron
    int check = 0; // chon nhac hay chua
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver(); // broadcast thong bao su kien


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("background", MODE_PRIVATE);
        String background = sharedPreferences.getString("background", "");

        if(background.length() > 0) {
            Drawable d = Drawable.createFromPath(background);
            RelativeLayout rl = findViewById(R.id.MusicPlay);
            rl.setBackground(d);
        }

        anhXa(); // set id
        addSong(); // data bai hat
        khoitao(); mediaPlayer.stop();

        adapter = new MyRecyclerViewAdapter(arraySong, this);
        adapter.setMysongListener(this);
        recyclerView.setAdapter(adapter);
        adapter.setSongListener(this);


        animation = AnimationUtils.loadAnimation(this, R.anim.disc_rotate); // animation quay

        hinh.clearAnimation(); // dung

        setTimeTotal();

        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                khoitao();
                ActionLoop();
                setTimeTotal();
                hinh.startAnimation(animation);
                thongbao();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check == 1) {
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        btnStop.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play));
                        hinh.clearAnimation();
                    } else {
                        mediaPlayer.start();
                        btnStop.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause));
                        hinh.startAnimation(animation);
                    }
                    setTimeTotal();
                    thongbao();

                    btnStop.setEnabled(true);
                } else {
                   Toast.makeText(MainActivity.this, "Chon nhac trong list", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position++;
                if(position > arraySong.size()-1) position = 0;
                if(mediaPlayer.isPlaying()) mediaPlayer.stop();
                khoitao();
                setTimeTotal();
                updateTimeSong();
                thongbao();
                check = 1;
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                position--;
                if(position < 0) position = arraySong.size()-1;
                if(mediaPlayer.isPlaying()) mediaPlayer.stop();
                khoitao();
                setTimeTotal();
                updateTimeSong();
                thongbao();
                check = 1;
            }
        });

        findViewById(R.id.addMusicBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
    }


    // cac ham


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // hien thi menu option
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // bat su kien xu ly item menu
        int id = item.getItemId();
        switch (id){
            case R.id.mnNote:
                Intent intentNote = new Intent(MainActivity.this, NoteActivity.class);
                mediaPlayer.stop();
                startActivity(intentNote);
                break;

            case R.id.mnInfor:
                Intent in = new Intent(MainActivity.this, setBackground.class);
                mediaPlayer.stop();
                startActivity(in);
                break;
            //return super.onOptionsItemSelected(item);
            case R.id.mnKara:
                Intent intent = new Intent(MainActivity.this, KaraokeActivity.class);
                mediaPlayer.stop();
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), "Karaoke", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRecordActivity:
                Intent intentRecord = new Intent(MainActivity.this,RecordActivity.class);
                mediaPlayer.stop();
                startActivity(intentRecord);
                break;
            //return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void thongbao(){
        Song song = null;
        if(position>=0) {
             song = arraySong.get(position);
        }
        Intent intentPause = new Intent("comm.cta.notificationMusicPlayerPause"); // pause
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, intentPause, 0);

        Intent intentNext = new Intent("comm.cta.notificationMusicPlayerNext"); // next
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, intentNext, 0);

        Intent intentPre = new Intent("comm.cta.notificationMusicPlayerPre"); // next
        PendingIntent PrePending = PendingIntent.getBroadcast(this, 0, intentPre, 0);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cta);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_audio)
                .setSubText("MediaPlayer")
                .setContentTitle(song.getTitle())
                .setContentText("SooBin")
                .setLargeIcon(bitmap)
                .addAction(R.drawable.ic_pre, "Previous", PrePending) // 0
                .addAction(R.drawable.ic_pause, "Pause",  pausePending) // 1
                .addAction(R.drawable.ic_next, "Next", nextPending) // 2
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1))
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1, notification);

    }

    private void addSong(){
        arraySong = new ArrayList<>();

//        Class raw = R.raw.class;
//        Field[] fields = raw.getFields();
//        for (Field field : fields) {
//            try {
//                Log.i("REFLECTION",
//                        String.format("%s is %d", field.getName(), field.getInt(null)));
//                arraySong.add(new Song(field.getName(), "" + field.getInt(null)));
//                ghiFile("" + field.getInt(null), field.getName());
//            } catch (IllegalAccessException e) {
//                Log.e("REFLECTION", String.format("%s threw IllegalAccessException.",
//                        field.getName()));
//            }
//        }

        arraySong.add(new Song("Mai xa", R.raw.maixa +""));
        arraySong.add(new Song("Cu chill thoi", R.raw.cuchillthoi +""));
        arraySong.add(new Song("Nang am xa dan", R.raw.nangamxadan+""));
        arraySong.add(new Song("Em cua ngay hom qua", R.raw.emcuangayhq+""));
//        ghiArray();
        docFile();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void khoitao(){ // khoi tao bai hat hien tai
        try {
            int resID = Integer.parseInt(arraySong.get(position).getFile());
            mediaPlayer = MediaPlayer.create(MainActivity.this, resID);
            //mediaPlayer.start();
        }
        catch (Exception exception) {
            mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(arraySong.get(position).getFile()));
            //mediaPlayer.start();
        }
        mediaPlayer.start();
        if (arraySong.size() > 0) txtTitle.setText((position + 1) + ". " + arraySong.get(position).getTitle());

    }


    private void updateTimeSong(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                txtTimeSong.setText(dinhDangGio.format(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 500);
                // kiem tra bai hat ket thuc
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        position++;
                        if(position > arraySong.size()-1) position = 0;
                        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
                        khoitao();
                        setTimeTotal();
                        updateTimeSong();
                    }
                });
            }
        },100);


    }

    private void setTimeTotal(){ // thoi gian bai hat
        SimpleDateFormat dinhdanggio = new SimpleDateFormat("mm:ss");
        txtTimeTotal.setText(dinhdanggio.format(mediaPlayer.getDuration()));
        skSong.setMax(mediaPlayer.getDuration());
        skSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { // tua nhac
                mediaPlayer.seekTo(skSong.getProgress());
            }
        });
    }

    private void ActionLoop(){ // hien thi dinh dang thoi gian
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
                txtTimeSong.setText(dinhDangGio.format(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 500);
                //kiem tra bai hat ket thuc
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
                        khoitao();
                        mediaPlayer.start();
                        setTimeTotal();
                        mediaPlayer.start();
                        ActionLoop();
                    }
                });
            }
        },100);
    }

    private void anhXa(){
        txtTimeSong = findViewById(R.id.textViewTimeSong);
        txtTimeTotal = findViewById(R.id.textViewTimeTotal);
        txtTitle = findViewById(R.id.textviewTitle);
        txtTimeTotal = findViewById(R.id.textViewTimeTotal);
        txtTitle = findViewById(R.id.textviewTitle);
        skSong = findViewById(R.id.seekBarSong);
        btnPrev = findViewById(R.id.imageButtonPre);
        btnStop = findViewById(R.id.imageBottonPause);
        btnNext = findViewById(R.id.imageBottonNext);
        btnLoop = findViewById(R.id.songLoop);
        hinh = findViewById(R.id.imageViewDisc);
        recyclerView = findViewById(R.id.recyclerView);
    }


    @Override
    public void play() {
        position = adapter.index;
        check = 1;
        if(mediaPlayer.isPlaying()){
            btnStop.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause));
            hinh.startAnimation(animation);
            mediaPlayer.stop();
            khoitao();
            updateTimeSong();
            setTimeTotal();
            check = 1;
            btnStop.setEnabled(true);
        }else
        if(!mediaPlayer.isPlaying()){
            btnStop.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause));
            hinh.startAnimation(animation);
            khoitao();
            updateTimeSong();
            setTimeTotal();
            check = 1;
            btnStop.setEnabled(true);
        }
        thongbao();
    }

    @Override
    protected void onResume() { // khai bao intentFilter
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("comm.cta.notificationMusicPlayerPause");
        intentFilter.addAction("comm.cta.notificationMusicPlayerNext");
        intentFilter.addAction("comm.cta.notificationMusicPlayerPre");
        registerReceiver(myBroadcastReceiver, intentFilter); // toan cuc
        myBroadcastReceiver.setMyBroadcastLis(this);
        super.onResume();
    }

    @Override
    public void pause() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            btnStop.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play));
            hinh.clearAnimation();
        } else {
            mediaPlayer.start();
            btnStop.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause));
            hinh.startAnimation(animation);
        }
        setTimeTotal();
        thongbao();
    }

    @Override
    public void next() {
        position++;
        if(position > arraySong.size()-1) position = 0;
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
        khoitao();
        setTimeTotal();
        updateTimeSong();
        thongbao();
    }

    @Override
    public void pre() {
        mediaPlayer.pause();
        position--;
        if(position < 0) position = arraySong.size()-1;
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
        khoitao();
        setTimeTotal();
        updateTimeSong();
        thongbao();
    }



    private final int FILE_SELECT_CODE = 0;
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",Toast.LENGTH_SHORT).show();
        }

    }


    private void docFile() {
        try {
            Context context=this;
            Scanner scanner = new Scanner(new File(context.getFilesDir() + "data.txt"));
            while (scanner.hasNextLine()){
                String tenBai = scanner.nextLine();
                String uriFile = scanner.nextLine();
                Uri uri = Uri.parse(uriFile);
                Song song = new Song(tenBai, uriFile);
                arraySong.add(song);
            }

        } catch (Exception e) {
            Log.i("TAG+===", "docFile: loi");
        }
    }


    private void ghiFile(String s,String tenBai){

//        if(arraySong.size()>0){
//            for(int i=0;i<arraySong.size();i++){
//                if(arraySong.get(i).getTitle().equals(tenBai)&&arraySong.get(i).getFile().equals(s)){
//                    return;
//                }
//
//            }
//        }
        try {
            Context context=this;
            File file = new File(context.getFilesDir()+"data.txt");
//            file.delete();
            FileWriter fr = new FileWriter(file, true);
            fr.write(tenBai);fr.write("\n");
            fr.write(s);fr.write("\n");
            fr.close();
        }
        catch (Exception e){
        }
    } // ghi duong dan file duoc add vao may

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                    Uri returnUri = data.getData();
                    String mimeType = getContentResolver().getType(returnUri);
                    Cursor returnCursor =getContentResolver().query(returnUri, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    Uri uri = data.getData();
                    ghiFile(returnUri.toString(),returnCursor.getString(nameIndex));
                    docFile();
                    adapter.listsong=arraySong;
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public void ghiArray() {
        try {
            Context context = this;
            File file = new File(context.getFilesDir() + "data.txt");
            FileWriter fr = new FileWriter(file);

            for (Song song : arraySong) {
                Log.i("TAG", "ghiArray: " + song.getTitle() + " " + song.getFile());
                fr.write(song.getTitle());
                fr.write("\n");
                fr.write(song.getFile());
                fr.write("\n");
            }
            fr.close();
        } catch (Exception exception) {

        }
    }

    @Override
    public void delete(int position) {
        txtTitle.setText("");
        adapter.listsong = arraySong;
        adapter.listsong.remove(position);
        adapter.notifyDataSetChanged();
        ghiArray();
        docFile();
    }
}