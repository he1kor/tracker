package com.helkor.project.connection.websocket;

import com.helkor.project.activities.MainActivity;
import com.helkor.project.dialogs.RouteSendingDialogFragment;
import com.helkor.project.dialogs.util.TokenListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteSender implements TokenUsable, TokenListAdapter.Listener, RouteSendingDialogFragment.Listener{
    private List<String> tokens = new ArrayList<>(Arrays.asList("ABTQW","QWYSA","TQWJA","JTFCD"));
    MainActivity mainActivity;
    RouteSendingDialogFragment routeSendingDialogFragment;
    public RouteSender(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
    public void goToRouteSender(){
        routeSendingDialogFragment = new RouteSendingDialogFragment(this);
        routeSendingDialogFragment.show(mainActivity.getSupportFragmentManager(),"routeSendingDialogFragment");
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
        System.out.println(tokens.remove(position) + " is deleted");
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
        removeToken(token);
    }

    @Override
    public void onRejectClick(String token) {
        removeToken(token);
    }

    @Override
    public void onCancel() {

    }
}
