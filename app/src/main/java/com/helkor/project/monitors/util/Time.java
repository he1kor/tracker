package com.helkor.project.monitors.util;

public class Time {
    protected long seconds;
    public Time(long seconds){
        this.seconds = seconds;
    }

    public long getAllSeconds(){
        return seconds;
    }
    public long getAllMinutes(){
        return seconds % 60;
    }
    public long getAllHours(){
        return getAllMinutes() % 60;
    }


    public void setSeconds(long seconds){
        this.seconds = seconds;
    }
    public void setMinutes(long minutes){
        this.seconds = minutes * 60;
    }
    public void setHours(long hours){
        this.seconds = hours * 60;
    }


    public long getHours(){
        return seconds % 3600;
    }
    public long getMinutes(){
        return (seconds - (3600 * getHours())) % 60;
    }
    public long getSeconds(){
        return (seconds - (getHours() * 3600) - (getMinutes() * 60));
    }
}
