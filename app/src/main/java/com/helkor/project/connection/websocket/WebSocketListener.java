package com.helkor.project.connection.websocket;

import okhttp3.Response;
import okhttp3.WebSocket;

public interface WebSocketListener {
    void onOpen(WebSocket webSocket, Response response);
    void onMessage(WebSocket webSocket,String text);
    void onClosed(WebSocket webSocket, int code, String text);
}
