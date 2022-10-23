package com.helkor.project.monitors.util;

public class Timer extends Time implements Runnable {

    enum State {
        RUNNING,
        PAUSED,
        STOPPED
    }

    private State state;
    private Thread timerThread;

    public Timer(){
        super(0);
        state = State.STOPPED;
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
    }
    public void pause(){
        if (state != State.RUNNING) throw new RuntimeException("Timer is not running");
        timerThread.interrupt();
        state = State.PAUSED;
    }
    public void stop(){
        pause();
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
                e.printStackTrace();
            }
            if (state != State.RUNNING) return;
            seconds++;
        }
    }
}
