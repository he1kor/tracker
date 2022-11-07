package com.helkor.project.connection.websocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class WebSocketController{
    private WebSocket webSocket;
    private WaitingConnectionWebSocketListener waitingConnectionWebSocketListener;
    public WebSocketController(){
    }
    public void setWebSocketConnection(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://doggygo.herokuapp.com/ws").build();
        waitingConnectionWebSocketListener = new WaitingConnectionWebSocketListener();
        webSocket = client.newWebSocket(request, waitingConnectionWebSocketListener);

    }
    public void close(String message){
        webSocket.close(1000,message);
    }
    public void sendMessage(String message){
        webSocket.send(message);
    }
    public void setWebSocketListener(Object implementationContext){
        System.out.println(waitingConnectionWebSocketListener == null);
        System.out.println(implementationContext == null);
        waitingConnectionWebSocketListener.setWebSocketListener(implementationContext);
    }
    public boolean getWebSocketStatus(){
        if (waitingConnectionWebSocketListener == null) return false;
        return waitingConnectionWebSocketListener.isRunning();
    }
}
