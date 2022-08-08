package com.example.musicplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapter.MyRecyclerViewAdapter;
import com.example.musicplayer.adapter.SongListener;
import com.example.musicplayer.model.Song;
import com.example.musicplayer.utils.VideoViewUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Scanner;

public class RecordActivity extends AppCompatActivity implements MyRecyclerViewAdapter.MysongListener , SongListener{
    private ArrayList<Song> arraySong;
    private ArrayList<Song> arrayRecord;
    static MyRecyclerViewAdapter adapter ;
    RecyclerView recyclerView;
    private MediaController mediaController; // trinh phat video
    private int position = 0;
    static String LOG_TAG = "RecordActivity";
    static private MediaPlayer player = null;
    static private VideoView videoView = null;
    static private MediaPlayer musicPlayer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_layout);
        anhxa();
        addSong();
        docFile();
        recyclerView = findViewById(R.id.recyclerViewRecord);
        adapter = new MyRecyclerViewAdapter(arrayRecord, this);
        adapter.setMysongListener(this);
        recyclerView.setAdapter(adapter);
        adapter.setSongListener(this);
//        this.mediaController = new MediaController(RecordActivity.this);
//        this.mediaController.setAnchorView(videoView);
//        this.videoView.setMediaController(mediaController); // controll video

    }
    private void addSong(){
        arraySong = new ArrayList<>();
        arrayRecord = new ArrayList<>();
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

        recyclerView = findViewById(R.id.recyclerViewRecord);
        player = new MediaPlayer();
        musicPlayer = new MediaPlayer();
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
        arrayRecord=new ArrayList<>();
        arraySong = new ArrayList<>();
        Log.i("TAG+===", "docFile: ");
        try {
            Context context=this;
            Scanner scanner = new Scanner(new File(context.getFilesDir() + "record.txt"));
            while (scanner.hasNextLine()){
                String recordName = scanner.nextLine();
                String recordFile = scanner.nextLine();
                String tenBai = scanner.nextLine();
                String startMusicPos = scanner.nextLine();

                Song song = new Song(tenBai,startMusicPos);
                Song record = new Song(recordName, recordFile);
                arraySong.add(song);
                arrayRecord.add(record);


            }

        } catch (Exception e) {
            Log.i("TAG+===", "docFile: loi");
        }
    }
    @Override
    public void play() {
        player.stop();
        musicPlayer.stop();
        player.release();
        musicPlayer.release();
        position = adapter.index;
        Song song = arraySong.get(position);
        Song record = arrayRecord.get(position);
        player = new MediaPlayer();
        musicPlayer = new MediaPlayer();
        int id = VideoViewUtils.getRawResIdByName( this, song.getTitle());

        Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + id);
        String musicFileSource= "android.resource://" + this.getPackageName() + "/" +id;
        Log.d("source uri", "play: " + musicFileSource);
        String recordFileSource = record.getFile();
        try {
            musicPlayer =MediaPlayer.create((Context) this,uri);
            player.setDataSource(recordFileSource);
            player.setVolume(15,15);
            player.prepare();
            player.start();
            musicPlayer.seekTo(Integer.parseInt(song.getFile()));
            musicPlayer.setVolume(1,1);
            musicPlayer.start();
            Log.d(LOG_TAG, "startPlaying: "  + player.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "prepare() failed: " + e);
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                musicPlayer.stop();
            }
        });
    }

    public void ghiArray(){
        try {
            Context context=this;
            File file = new File(context.getFilesDir()+"record.txt");
            FileWriter fr = new FileWriter(file);

            for(int i = 0; i < arraySong.size();i++){
                fr.write(arrayRecord.get(i).getTitle());fr.write("\n");
                fr.write(arrayRecord.get(i).getFile());fr.write("\n");
                fr.write(arraySong.get(i).getTitle());fr.write("\n");
                fr.write(arraySong.get(i).getFile());fr.write("\n");
            }
            fr.close();
        }
        catch (Exception exception){

        }
    }
    @Override
    public void delete(int position) {

        adapter.listsong = arrayRecord;
        adapter.listsong.remove(position);
        adapter.notifyDataSetChanged();
//        arrayRecord.remove(position);
        arraySong.remove(position);
        ghiArray();
        docFile();
    }
}
