package com.helkor.project.dialogs.util;

public class MiniTimer implements Runnable{
    public interface Listener{

        void onTimerEnded();
        void onNextSecond(int seconds);
    }
    private Listener listener;
    private int seconds;
    private Thread timerThread;

    public MiniTimer(Object implementationContext){
        trySetListener(implementationContext);
    }
    private void trySetListener(Object implementationContext){
        try {
            listener = (Listener) implementationContext;
        } catch (ClassCastException e){
            throw new RuntimeException(implementationContext + " must implement Listener");
        }
    }
    public void startNew(int seconds){
        this.seconds = seconds;
        timerThread = new Thread(this);
        timerThread.start();
    }
    public void removeTimer(){
        if (timerThread != null) {
            timerThread.interrupt();
            System.out.println("timeThread removed");
        } else System.out.println("timeThread is null");
    }
    public boolean isRunning(){
        if (timerThread == null) return false;
        return timerThread.isAlive();
    }

    @Override
    public void run() {
        while (seconds > 0){
            listener.onNextSecond(seconds);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
            seconds--;
        }
        listener.onTimerEnded();
    }
}
