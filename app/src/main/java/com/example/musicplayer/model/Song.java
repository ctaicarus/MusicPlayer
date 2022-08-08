package com.example.musicplayer.model;

public class Song {
    private String Title;
    private String File;
    public Song(String title, String file){
        Title = title;
        File = file;
    }
    public String getTitle() {return Title;}
    public String getFile() {return File;}
}
