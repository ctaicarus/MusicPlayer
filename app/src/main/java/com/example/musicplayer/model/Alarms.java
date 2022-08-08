package com.example.musicplayer.model;

import java.io.Serializable;

public class Alarms implements Serializable { // class
    private int IdAlarm;
    private String alarm;
    private String note;
    private int check;

    public Alarms(int idAlarm, String alarm, String note, int check){
        this.IdAlarm = idAlarm;
        this.alarm = alarm;
        this.note  =note;
        this.check = check;
    }

    public int getIdAlarm() {
        return IdAlarm;
    }

    public void setIdAlarm(int idAlarm){
        IdAlarm = idAlarm;
    }

    public String getAlarm() {
        return alarm;
    }

    public String getNote() {
        return note;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCheck(int check) { this.check = check;}

    public int getCheck() {
        return check;
    }
}
