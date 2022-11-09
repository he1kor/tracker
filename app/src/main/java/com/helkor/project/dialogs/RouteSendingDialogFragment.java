package com.helkor.project.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.helkor.project.R;
import com.helkor.project.dialogs.util.DownloadModes;
import com.helkor.project.input.buttons.SwitchArrowButton;
import com.helkor.project.input.TokenEditText;
import com.helkor.project.input.buttons.Utils.LittleButton;
import com.helkor.project.dialogs.util.TokenListAdapter;

import java.util.Locale;

public class RouteSendingDialogFragment extends DialogFragment implements LittleButton.Listener,TokenEditText.Listener{

    @Override
    public void onTokenEntered(String text) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().findViewById(R.id.token_input).getWindowToken(), 0);
        enteredToken = text;
        listener.onTokenEntered(text);
    }

    @Override
    public void onNotTokenEntered(String text) {
        Toast.makeText(getActivity(),"Not a token",Toast.LENGTH_SHORT).show();
        listener.onNotTokenEntered(text);
    }

    @Override
    public void onEmptyEntered() {
        listener.onEmptyEntered();
    }

    @Override
    public void onClickLittleButton(View view) {
        switch (view.getId()){
            case R.id.button_switch_arrow:
                switchArrowButton.nextVariant();
                checkMode();
                break;
        }
    }


    public interface Listener{
        void onCancel();
        void onTokenEntered(String text);
        void onNotTokenEntered(String text);
        void onEmptyEntered();
        void onClearTokenButtonClick();
        void onAcceptRoute();
    }

    String myToken;
    String enteredToken;
    int seconds;
    DownloadModes downloadMode;

    RecyclerView recyclerView;
    TokenListAdapter adapter;

    TokenEditText tokenEditText;
    SwitchArrowButton switchArrowButton;
    ImageButton clearEnteredTokenButton;
    ImageButton acceptRouteButton;

    Object implementationContext;
    Listener listener;

    public RouteSendingDialogFragment(Object implementationContext, DownloadModes downloadMode, String enteredToken){
        this.implementationContext = implementationContext;
        trySetListener(implementationContext);
        this.downloadMode = downloadMode;
        System.out.println("download mode init " + downloadMode);
        this.enteredToken = enteredToken;
        System.out.println(this.enteredToken);
    }
    private void trySetListener(Object implementationContext){
        try {
            listener = (Listener) implementationContext;
        } catch (ClassCastException e){
            throw new RuntimeException(implementationContext + " must implement Listener");
        }
    }
    @Override
    public void onCancel (DialogInterface dialog){
        super.onCancel(dialog);
        listener.onCancel();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.route_connection_dialog_fragment,null,false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        recyclerView = view.findViewById(R.id.scroll_list);
        adapter = new TokenListAdapter(getActivity(), implementationContext);
        recyclerView.setAdapter(adapter);
        clearEnteredTokenButton = view.findViewById(R.id.clear_token_button);
        clearEnteredTokenButton.setOnClickListener(clearButton -> {
            listener.onClearTokenButtonClick();
        });
        acceptRouteButton = view.findViewById(R.id.accept_route);
        acceptRouteButton.setOnClickListener(acceptButton -> {
                    System.out.println("Click " + downloadMode);
                    if (downloadMode == DownloadModes.readyMode) {
                        listener.onAcceptRoute();
                    }
        }
        );

        tokenEditText = new TokenEditText(view.findViewById(R.id.token_input),this);

        switchArrowButton = new SwitchArrowButton(getActivity(),view,this,R.id.button_switch_arrow);

        if (myToken != null) {
            ((TextView) getView().findViewById(R.id.your_token_value)).setText(myToken);
        }
    }

    public void notifyItemRemoved(int index){
        adapter.notifyItemRemoved(index);
    }
    public void notifyItemInserted(int index){
        adapter.notifyItemInserted(index);
    }
    private void checkMode(){
        switch (switchArrowButton.getVariant()){
            case UPLOAD:
                setUploadMode();
                break;
            case DOWNLOAD:
                setDownloadMode();
                break;
        }
    }
    private void checkDownloadMode(){
        switch (downloadMode){
            case inputMode:
                setInputMode();
                break;
            case timerMode:
                setTimerMode();
                break;
            case readyMode:
                setReadyMode();
                break;
        }
    }
    private void setDownloadMode(){
        ((TextView) getView().findViewById(R.id.foreign_token_text)).setText("Input foreign token:");
        ((TextView) getView().findViewById(R.id.mode_text)).setText("Get route");

        getView().findViewById(R.id.scroll_list).setVisibility(View.GONE);
        getView().findViewById(R.id.token_input_layout_outside).setVisibility(View.VISIBLE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) getView().findViewById(R.id.constraintlayout));
        constraintSet.connect(R.id.middle_divider,ConstraintSet.TOP,R.id.token_input_layout_outside,ConstraintSet.BOTTOM,0);
        constraintSet.applyTo(getView().findViewById(R.id.constraintlayout));

        checkDownloadMode();
    }
    private void setUploadMode(){
        ((TextView) getView().findViewById(R.id.foreign_token_text)).setText("Foreign tokens' requests:");
        ((TextView) getView().findViewById(R.id.mode_text)).setText("Send route");

        getView().findViewById(R.id.scroll_list).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.token_input_layout_outside).setVisibility(View.GONE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) getView().findViewById(R.id.constraintlayout));
        constraintSet.connect(R.id.middle_divider,ConstraintSet.TOP,R.id.scroll_list,ConstraintSet.BOTTOM,0);
        constraintSet.applyTo(getView().findViewById(R.id.constraintlayout));
    }



    public void setInputMode(){
        enteredToken = "";
        System.out.println("enteredToken: " + enteredToken);
        EditText editText = (EditText) (getView().findViewById(R.id.token_input));
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setText(enteredToken);
        editText.setTextIsSelectable(true);

        getView().findViewById(R.id.clear_token_button).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.accept_route).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.request_timer).setVisibility(View.INVISIBLE);

    }
    public void setTimerMode() {

        EditText editText = (EditText) (getView().findViewById(R.id.token_input));
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setText(enteredToken);
        editText.setTextIsSelectable(false);

        getView().findViewById(R.id.clear_token_button).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.accept_route).setVisibility(View.VISIBLE);
        ((ImageButton) getView().findViewById(R.id.accept_route)).setImageAlpha(0);
        getView().findViewById(R.id.request_timer).setVisibility(View.VISIBLE);
        ((TextView) getView().findViewById(R.id.request_timer)).setText(String.valueOf(seconds));

    }
    public void setReadyMode(){
        System.out.println("ready mode");
        EditText editText = (EditText) (getView().findViewById(R.id.token_input));
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setText(enteredToken);
        editText.setTextIsSelectable(false);

        getView().findViewById(R.id.clear_token_button).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.accept_route).setVisibility(View.VISIBLE);
        ((ImageButton) getView().findViewById(R.id.accept_route)).setImageAlpha(255);
        getView().findViewById(R.id.request_timer).setVisibility(View.INVISIBLE);
    }
    public void setMyToken(String token){
        myToken = token.toUpperCase(Locale.ROOT);
        if (getView() != null) {
            ((TextView) getView().findViewById(R.id.your_token_value)).setText(token);
        }
    }
    public void setMiniTimerSeconds(int seconds){
        this.seconds = seconds;
        if (getView() != null) {
            ((TextView) getView().findViewById(R.id.request_timer)).setText(String.valueOf(seconds));
        }
    }
    public void setDownloadMode(DownloadModes downloadMode){
        this.downloadMode = downloadMode;
        if (getView() != null){
            checkDownloadMode();
        }
    }
}
