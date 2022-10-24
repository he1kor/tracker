package com.helkor.project.monitors.util;

import android.content.Context;
import android.util.Log;

public class Timer extends Time implements Runnable {

    private enum State {
        RUNNING,
        PAUSED,
        STOPPED
    }
    public interface Listener{
        public void onTimeUpdated(Time time);
    }

    private State state;
    private Thread timerThread;
    private Listener listener;

    public Timer(Object context){
        super(0);
        tryAddListener(context);
        state = State.STOPPED;
    }
    public Timer(){
        super(0);
        state = State.STOPPED;
    }
    private void tryAddListener(Object context){
        try{
            listener = (Listener) context;
        } catch (Exception e){
            throw new RuntimeException("Couldn't add listener to context");
        }
    }

    public void setListener(Listener listener){
        this.listener = listener;
    }

    public void start(){
        if (state != State.STOPPED) throw new RuntimeException("Timer is already running");
        reset();
        state = State.RUNNING;
        timerThread = new Thread(this);
        timerThread.start();
    }
    public void reset(){
        seconds = 0;
        listener.onTimeUpdated(new Time(seconds));
    }
    public void pause(){
        if (state != State.RUNNING) throw new RuntimeException("Timer is not running");
        timerThread.interrupt();
        state = State.PAUSED;
    }
    public void stop(){
        if (state != State.PAUSED) pause();
        state = State.STOPPED;
    }
    public void resume(){
        if (state != State.PAUSED) throw new RuntimeException("Timer is not paused");
        state = State.RUNNING;
        timerThread = new Thread(this);
        timerThread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.i("Timer: ", " stopped");
            }
            if (state != State.RUNNING) return;
            seconds++;
            if (listener != null) listener.onTimeUpdated(new Time(seconds));
        }
    }
}
