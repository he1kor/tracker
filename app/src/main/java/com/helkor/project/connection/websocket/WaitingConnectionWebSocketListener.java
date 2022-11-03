package com.helkor.project.connection.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;

public class WaitingConnectionWebSocketListener extends okhttp3.WebSocketListener {

    int NORMAL_CLOSING_STATUS = 1000;

    @Override
    public void onOpen(WebSocket webSocket, Response response){
        System.out.println("onOpen");
        webSocket.send("connected");
    }
    @Override
    public void onMessage(WebSocket webSocket, String text){
        System.out.println("onMessage: " + text);
    }
    @Override
    public void onClosed(WebSocket webSocket,int code,String text){
        webSocket.close(NORMAL_CLOSING_STATUS,null);
        System.out.println("onClose");
    }
}