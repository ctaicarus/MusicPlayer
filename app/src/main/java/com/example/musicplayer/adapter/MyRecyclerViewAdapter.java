package com.example.musicplayer.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.model.Song;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>  {

    public ArrayList<Song> listsong; // list data
    Context context;
    public int index;



    SongListener songListener;

    public void setSongListener(SongListener songListener) {
        this.songListener = songListener;
    }

    public interface MysongListener{ // interface lang nghe xu kien
        void play();
    }



    private MysongListener listener; // khai bao interface

    public void setMysongListener(MysongListener l){
        this.listener = l;
    } // set gia tri interface

    public MyRecyclerViewAdapter(ArrayList<Song> listsong, Context mContext) { // constructor
        this.listsong = listsong;
        this.context = mContext;
    }

    public void removeItem(int pos){ // remove
        listsong.remove(pos);
        notifyDataSetChanged();
    }

    public void play(){ // play

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mysong_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int pos = position;
        Song song = listsong.get(pos);
        holder.song.setText(song.getTitle());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, song.getTitle(), Toast.LENGTH_SHORT).show();
                if(listener != null){
                    index = pos;
                    listener.play();
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songListener.delete(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listsong.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout relativeLayout;
    TextView song;
    Button deleteButton;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        relativeLayout = itemView.findViewById(R.id.relative);
        song = itemView.findViewById(R.id.name_song);
        deleteButton=itemView.findViewById(R.id.deleteButton);
    }

}