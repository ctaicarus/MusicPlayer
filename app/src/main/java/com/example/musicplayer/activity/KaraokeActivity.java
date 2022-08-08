package com.example.musicplayer.activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;
import android.content.pm.PackageManager;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.*;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.VideoViewUtils;

public class KaraokeActivity extends AppCompatActivity implements MyRecyclerViewAdapter.MysongListener{
    private VideoView videoView;
    private Button btnrecord, btnstoprecord, btnplayrecord;
    private int position = 0;
    private MediaController mediaController; // trinh phat video
    static String LOG_TAG = "AndroidVideoView";
    ArrayList<Song> arraySong;
    static MyRecyclerViewAdapter adapter ;
    RecyclerView recyclerView;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private MediaRecorder recorder = null;
    private Button   btnPlay = null;
    static private MediaPlayer   player = null;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private String fileSource;
    private int startMusicPos,stopMusicPos;
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.reset();
            player.setDataSource(fileSource);
            Log.d(LOG_TAG, "file name save: " + fileSource);
            player.prepare();
            videoView.seekTo(startMusicPos);
            player.start();
            videoView.start();
            Log.d(LOG_TAG, "startPlaying: "  + player.getDuration());
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        Toast.makeText(this, "Play record", Toast.LENGTH_SHORT).show();
    }

    private void stopPlaying() {
        player.release();
        videoView.pause();
        player = null;
        Toast.makeText(this, "Stop record", Toast.LENGTH_SHORT).show();
    }

    private void startRecording() {

        Toast.makeText(this, "Start record", Toast.LENGTH_SHORT).show();
        findViewById(R.id.btnStopRecord).setEnabled(false);
        btnplayrecord.setEnabled(false);

        recorder = new MediaRecorder();
        startMusicPos = videoView.getCurrentPosition();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileSource);
        Log.d(LOG_TAG, "file name save: " + fileSource);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        videoView.start();
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        stopMusicPos = videoView.getCurrentPosition();
        videoView.pause();
        recorder.reset();
        recorder.release();

        recorder = null;


        findViewById(R.id.btnStopRecord).setEnabled(true);
        btnplayrecord.setEnabled(true);

        Toast.makeText(this, "Stop record", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        File file = new File(this.getFilesDir()+"record.txt");
//        file.delete();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_karaoke);
        anhxa();
        addSong();
        btnrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecord(mStartRecording);

                mStartRecording = !mStartRecording;
            }
        });
        btnplayrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(mStartPlaying);
                mStartPlaying = !mStartPlaying;
            }
        });
        findViewById(R.id.btnStopRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ghiFile(fileSource,fileName,arraySong.get(position).getTitle(),""+startMusicPos);
                Toast.makeText(KaraokeActivity.this, "Saved record "+fileName, Toast.LENGTH_SHORT).show();
                setSource();

            }
        });

        findViewById(R.id.btnStopRecord).setEnabled(false);
        btnplayrecord.setEnabled(false);


        this.mediaController = new MediaController(KaraokeActivity.this);
        this.mediaController.setAnchorView(videoView);
        this.videoView.setMediaController(mediaController); // controll video
        adapter = new MyRecyclerViewAdapter(arraySong, this);
        adapter.setMysongListener(this);
        recyclerView.setAdapter(adapter);
        setSource();
        Log.d(LOG_TAG, "onCreate: " + fileName);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String RAW_VIDEO_SAMPLE = "videotest";
                playRawVideo(KaraokeActivity.this, videoView, RAW_VIDEO_SAMPLE); // phat video
            }
        });

    }
    private void setSource(){
        fileName = "audiorecord" + (docFile()+1) + ".3gp";
        fileSource = getExternalCacheDir().getAbsolutePath() + "/" + fileName;
    }
    private void addSong(){
        arraySong = new ArrayList<>();
        arraySong = new ArrayList<>();
        Class raw = R.raw.class;
        Field[] fields = raw.getFields();
        for (Field field : fields) {
            try {
                Log.i("REFLECTION",
                        String.format("%s is %d", field.getName(), field.getInt(null)));
                arraySong.add(new Song(field.getName(),""+field.getInt(null)));
            } catch(IllegalAccessException e) {
                Log.e("REFLECTION", String.format("%s threw IllegalAccessException.",
                        field.getName()));
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }
    private void anhxa() {
        videoView = (VideoView)findViewById(R.id.videoView);
        btnrecord = findViewById(R.id.btnrecord);
        btnplayrecord = findViewById(R.id.buttonPlayRecord);
        btnstoprecord = findViewById(R.id.btnStopRecord);
        recyclerView = findViewById(R.id.recyclerViewKaraoke);
    }

    public static void playRawVideo(Context context, VideoView videoView, String resName)  { // ID of video file.
        try {
            int id = getRawResIdByName(context, resName);
            Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + id);
            Log.i(LOG_TAG, "Video URI: "+ uri);
            videoView.setVideoURI(uri);
            videoView.requestFocus();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error Play Video: "+e.getMessage());
            Toast.makeText(context,"Error Play Video: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static int getRawResIdByName(Context context, String resName) { // tra lai vi tri file trong android
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName, "raw", pkgName);

        Log.i(LOG_TAG, "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.backtomain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // bat su kien xu ly item menu
        int id = item.getItemId();
        switch (id){
            case R.id.mnbacktomain:
                Intent intent = new Intent(KaraokeActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRecordActivity:
                Intent intentRC = new Intent(KaraokeActivity.this,RecordActivity.class);
                startActivity(intentRC);
                break;
        }
        return true;
    }

    @Override
    public void play() {
        position = adapter.index;
        Song song = arraySong.get(position);
        VideoViewUtils.playRawVideo(this,videoView,song.getTitle());
        videoView.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
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


    private int docFile() {
        int res = 0;
        Log.i("TAG+===", "docFile: ");
        try {
            Context context=this;
            Scanner scanner = new Scanner(new File(context.getFilesDir() + "record.txt"));
            while (scanner.hasNextLine()){
                String tenBai = scanner.nextLine();
                String uriFile = scanner.nextLine();
                String recordName= scanner.nextLine();
                String recordFile = scanner.nextLine();
                Uri uri = Uri.parse(uriFile);
                Song song = new Song(tenBai, uriFile);
                res++;

            }

        } catch (Exception e) {
            Log.i("TAG+===", "docFile: loi");
        }
        Log.d("ress", "docFile: " + res);
        return res;
    }


    public void ghiFile(String s,String tenBai,String musicName, String musicPos){

        if(arraySong.size()>0){
            for(int i=0;i<arraySong.size();i++){
                if(arraySong.get(i).getTitle().equals(tenBai)&&arraySong.get(i).getFile().equals(s)){
                    return;
                }

            }
        }
        try {
            Context context=this;
            File file = new File(context.getFilesDir()+"record.txt");
            //file.delete();
            FileWriter fr = new FileWriter(file, true);
            fr.write(tenBai);fr.write("\n");
            fr.write(s);fr.write("\n");
            fr.write(musicName);fr.write("\n");
            fr.write(musicPos);fr.write("\n");
            fr.close();
        }
        catch (Exception e){
        }
    }

}