package com.helkor.project.connection.websocket;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;

public class WaitingConnectionWebSocketListener extends okhttp3.WebSocketListener {

    private WebSocketListener webSocketListener;
    protected boolean isRunning;

    int NORMAL_CLOSING_STATUS = 1000;

    public WaitingConnectionWebSocketListener() {
        isRunning = false;
    }
    public void setWebSocketListener(Object implementationContext){
        try {
            webSocketListener = (WebSocketListener) implementationContext;
        } catch (ClassCastException e){
            Log.e(implementationContext.toString(),"Class must implement WebSocketListener");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response){
        super.onOpen(webSocket,response);
        isRunning = true;
        System.out.println("onOpen");
        webSocketListener.onOpen(webSocket,response);
    }
    @Override
    public void onMessage(WebSocket webSocket, String text){
        super.onMessage(webSocket,text);
        System.out.println("onMessage: " + text);
        webSocketListener.onMessage(webSocket,text);
    }
    @Override
    public void onClosing(WebSocket webSocket,int code,String text){
        super.onClosing(webSocket,code,text);
        isRunning = false;
        System.out.println("onClose");
        webSocketListener.onClosed(webSocket,code,text);
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }
    public boolean isRunning() {
        return isRunning;
    }
}
