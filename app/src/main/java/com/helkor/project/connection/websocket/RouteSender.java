package com.helkor.project.connection.websocket;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.helkor.project.connection.websocket.util.Command;
import com.helkor.project.connection.websocket.util.serialized.Message;
import com.helkor.project.connection.websocket.util.serialized.Point;
import com.helkor.project.connection.websocket.util.serialized.RouteData;
import com.helkor.project.dialogs.RouteSendingDialogFragment;
import com.helkor.project.dialogs.util.DownloadModes;
import com.helkor.project.dialogs.util.MiniTimer;
import com.helkor.project.dialogs.util.TokenListAdapter;
import com.helkor.project.draw.LineDrawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Response;
import okhttp3.WebSocket;

public class RouteSender implements WebSocketListener, TokenUsable, TokenListAdapter.Listener, RouteSendingDialogFragment.Listener,MiniTimer.Listener{

    private List<String> tokens = new ArrayList<>(Arrays.asList());
    private String enteredToken;
    private Activity activity;
    private RouteSendingDialogFragment routeSendingDialogFragment;
    private WebSocketController webSocketController;
    private boolean waitingForRoute;
    private MiniTimer miniTimer;
    private LineDrawer lineDrawer;
    private String myToken;
    private DownloadModes downloadMode;
    private List<com.yandex.mapkit.geometry.Point> route;

    public RouteSender(LineDrawer lineDrawer,Activity activity){
        this.activity = activity;
        webSocketController = new WebSocketController();
        webSocketController.setWebSocketConnection();
        webSocketController.setWebSocketListener(this);
        waitingForRoute = false;
        downloadMode = DownloadModes.inputMode;
        miniTimer = new MiniTimer(this);
        this.lineDrawer = lineDrawer;
    }

    public void goToRouteSender(AppCompatActivity activity){
        System.out.println(downloadMode + " " + enteredToken);
        if (downloadMode == DownloadModes.inputMode) {
            enteredToken = "";
        }
        routeSendingDialogFragment = new RouteSendingDialogFragment(this,downloadMode,enteredToken);
        routeSendingDialogFragment.show(activity.getSupportFragmentManager(),"routeSendingDialogFragment");
        if (myToken != null) {
            routeSendingDialogFragment.setMyToken(myToken);
        }
    }
    @Override
    public String getToken(int index){
        return tokens.get(index);
    }
    @Override
    public int getTokenAmount() {
        return tokens.size();
    }

    private void removeToken(String token){
        int position = tokens.indexOf(token);
        if (position == -1) return;
        tokens.remove(position);
        routeSendingDialogFragment.notifyItemRemoved(position);
    }
    private void insertToken(String token,int index){
        tokens.add(index, token);
        routeSendingDialogFragment.notifyItemInserted(index);
    }
    private void addToken(String token){
        tokens.add(token);
        routeSendingDialogFragment.notifyItemInserted(tokens.size()-1);
    }
    @Override
    public void onAcceptClick(String token) {
        RouteData routeData = new RouteData(lineDrawer.getRoute().getPoints(),token.toLowerCase(Locale.ROOT));
        webSocketController.sendMessage(Message.toJson(Command.c_sendRoute,new Gson().toJson(routeData)));
        removeToken(token);
    }

    @Override
    public void onRejectClick(String token) {
        removeToken(token);
    }



    @Override
    public void onCancel() {
    }

    @Override
    public void onTokenEntered(String token) {
        if (Objects.equals(token, myToken)) return;
        enteredToken = token;
        miniTimer.startNew(60);
        downloadMode = DownloadModes.timerMode;
        routeSendingDialogFragment.setDownloadMode(downloadMode);
        webSocketController.sendMessage(Message.toJson(Command.c_requestRoute, token.toLowerCase(Locale.ROOT)));
        waitingForRoute = true;
    }

    @Override
    public void onNotTokenEntered(String text) {

    }

    @Override
    public void onEmptyEntered() {

    }

    @Override
    public void onClearTokenButtonClick() {
        route = null;
        miniTimer.removeTimer();
        webSocketController.sendMessage(Message.toJson(Command.c_cancelRouteRequest,enteredToken.toLowerCase(Locale.ROOT)));
        enteredToken = "";
        downloadMode = DownloadModes.inputMode;
        routeSendingDialogFragment.setDownloadMode(downloadMode);
    }

    @Override
    public void onAcceptRoute() {
        lineDrawer.createRoute(route);
        miniTimer.removeTimer();
        route = null;
        enteredToken = "";
        downloadMode = DownloadModes.inputMode;
        routeSendingDialogFragment.setDownloadMode(downloadMode);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if (text.equals("ping!")) return;
        Message message = Message.fromJson(text);
        switch (message.getCommand()){
            case s_sendRoute:
                getRoute(message.getData());
                break;
            case s_notifyRouteRequested:
                getRouteRequest(message.getData());
                break;
            case s_notifyRouteRequestCanceled:
                removeTokenRequest(message.getData());
                break;
            case s_sendRandomToken:
                getRandomToken(message.getData());
                break;
        }
    }


    @Override
    public void onClosed(WebSocket webSocket, int code, String text) {
    }

    private void getRoute(String data){
        route = Point.convertPOJOToMapkit(new Gson().fromJson(data, Point[].class));
        downloadMode = DownloadModes.readyMode;
        routeSendingDialogFragment.setDownloadMode(downloadMode);
    }
    private void getRouteRequest(String data){
        System.out.println("Route requested!\n" + data);
        routeSendingDialogFragment.getActivity().runOnUiThread(() -> addToken(data.toUpperCase(Locale.ROOT)));
    }
    private void removeTokenRequest(String data){
        System.out.println("Removed token!\n" + data);
        removeToken(data);
    }
    private void getRandomToken(String data) {
        myToken = data;
        if (routeSendingDialogFragment != null){
            routeSendingDialogFragment.setMyToken(myToken);
        }
    }

    @Override
    public void onTimerEnded() {
        downloadMode = DownloadModes.inputMode;
        activity.runOnUiThread(() ->
                routeSendingDialogFragment.setDownloadMode(downloadMode));
    }

    @Override
    public void onNextSecond(int seconds) {
        routeSendingDialogFragment.setMiniTimerSeconds(seconds);
    }
}
