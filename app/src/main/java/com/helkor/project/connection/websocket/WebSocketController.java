package com.helkor.project.connection.websocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class WebSocketController {
    public WebSocketController(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://doggygo.herokuapp.com/waitconnection").build();
        WaitingConnectionWebSocketListener waitingConnectionWebSocketListener = new WaitingConnectionWebSocketListener();
        WebSocket webSocket = client.newWebSocket(request, waitingConnectionWebSocketListener);
        client.dispatcher().executorService().shutdown();
    }
}
