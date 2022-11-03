package com.helkor.project.global;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.helkor.project.R;
import com.helkor.project.activities.MainActivity;
import com.helkor.project.buttons.ClearButton;
import com.helkor.project.buttons.ConnectionButton;
import com.helkor.project.buttons.SwitchInputButton;
import com.helkor.project.buttons.Utils.LittleButton;
import com.helkor.project.buttons.MainButton;
import com.helkor.project.connection.websocket.RouteSender;
import com.helkor.project.connection.websocket.WebSocketController;
import com.helkor.project.dialogs.ClearConfirmDialogFragment;
import com.helkor.project.dialogs.LeaveWalkingConfirmDialogFragment;
import com.helkor.project.dialogs.RouteSendingDialogFragment;
import com.helkor.project.dialogs.util.TokenListAdapter;
import com.helkor.project.draw.LineDrawer;
import com.helkor.project.draw.LocationSensor;
import com.helkor.project.draw.TouchSensor;
import com.helkor.project.graphics.Background;
import com.helkor.project.graphics.Bar;
import com.helkor.project.map.MapState;
import com.helkor.project.map.NavigatorState;
import com.helkor.project.monitors.CounterMonitor;
import com.helkor.project.monitors.util.PathString;
import com.helkor.project.monitors.util.Time;
import com.helkor.project.monitors.util.Timer;
import com.yandex.mapkit.MapKit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Controller implements LocationSensor.CameraInitListener,LineDrawer.FinishListener,Timer.Listener, MainButton.Listener,LittleButton.Listener, ClearConfirmDialogFragment.Listener, LeaveWalkingConfirmDialogFragment.Listener {

    public Activity getActivity() {
        return main_activity;
    }

    protected final MainActivity main_activity;
    protected final MapKit map_kit;

    protected RouteSender routeSender;
    protected ConnectionButton button_connection;

    protected final MapState map_state;
    protected NavigatorState navigator_state;

    protected LineDrawer line_drawer;

    protected LocationSensor location_sensor;
    protected TouchSensor map_sensor;

    protected CounterMonitor counter_monitor;
    protected Timer timer;
    protected PathString path_string;

    protected MainButton main_button;
    protected SwitchInputButton button_switch_input;
    protected ClearButton button_clear;

    protected ModeState mode_state;
    String baseUrl = "https://doggygo.herokuapp.com/";

    protected final float COMFORTABLE_ZOOM_LEVEL = 17.5f;

    public Controller(MainActivity activity,MapKit map_kit){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.main_activity = activity;
        this.map_kit = map_kit;
        map_state = new MapState(getActivity().findViewById(R.id.map),map_kit);
        mode_state = new ModeState(this, ModeState.Mode.MAIN);
        initRetrofit();
    }
    private void initRetrofit(){
        Runnable retrofit = new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(baseUrl)
                        .build();
                HelloWorldService service = retrofit.create(HelloWorldService.class);
                Call<String> call = service.sendText("test","qest2");
                String result = "testqw";
                try {
                    Response<String> response = call.execute();
                    System.out.println("QWERT " + response.code());
                    result = response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String finalResult = result;
                main_activity.runOnUiThread(new Runnable() {
                    public void run() {
                        System.out.println(finalResult);
                    }
                });
            }
        };
        new Thread(retrofit).start();
        new WebSocketController();
    }

    public void setup(){

        //routeSender = new RouteSender(main_activity);

        counter_monitor = new CounterMonitor(main_activity,R.id.pedometer_layout_outside,R.id.pedometer_layout,R.id.pedometer_text1,R.id.pedometer_text2);
        timer = new Timer(this);
        path_string = new PathString(counter_monitor, PathString.BOTTOM);
        line_drawer = new LineDrawer(map_state);

        navigator_state = new NavigatorState(getActivity(),map_state);
        location_sensor = new LocationSensor(line_drawer,this,map_state.getMapView(),map_kit,COMFORTABLE_ZOOM_LEVEL);
        map_sensor = new TouchSensor(line_drawer,getActivity().findViewById(R.id.relative_layout_1),map_state.getMapView());

        line_drawer.addPathListener(path_string);
        line_drawer.addFinishListener(this);
        Background.loadAnimationLoadingText(main_activity);
    }

    @Override
    public void onCameraInitialized() {
        initButtons();
    }
    public void initButtons (){
        Background.setLoaded(true);
        map_state.show();
        Background.vanishing(main_activity);
        Bar.setActivity(main_activity);
        Bar.animateColor(main_activity.getColor(R.color.black),main_activity.getColor(R.color.light_red),2000);

        counter_monitor.animateAppearing();

        main_button = new MainButton(main_activity,this,R.id.button_start, R.anim.button_float);

        button_switch_input = new SwitchInputButton(main_activity,this,R.id.button_switch_input,R.anim.switch_input_button_float_show,R.anim.switch_input_button_float_hide);
        button_clear = new ClearButton(main_activity,this,R.id.button_clear,R.anim.clear_button_float_show,R.anim.clear_button_float_hide);
        //button_connection = new ConnectionButton(main_activity,this,R.id.button_connection,R.anim.connection_button_float_show,R.anim.connection_button_float_hide);

        mode_state.changeMode(ModeState.Mode.MAIN);
    }

    @Override
    public void onHoldBigButton() {
        holdMainButtonGetOutOfState();
        Bar.stop();
    }

    @Override
    public void onClickBigButton() {
        clickMainButtonGetOutOfState();
        Bar.stop();
    }

    private void holdMainButtonGetOutOfState(){
        switch (main_button.getVariant()) {
            case DRAW:
            case FINISH:
            case PAUSE:
            case WALK:
            case VIEW:
                mode_state.changeMode(ModeState.Mode.MAIN);
                break;
            case MAIN:
                mode_state.changeMode(ModeState.Mode.DRAW);
                break;
        }
    }
    private void clickMainButtonGetOutOfState(){
        switch (main_button.getVariant()) {
            case DRAW:
                mode_state.changeMode(ModeState.Mode.VIEW);
                break;
            case VIEW:
                mode_state.changeMode(ModeState.Mode.DRAW);
                break;
            case WALK:
                mode_state.changeMode(ModeState.Mode.PAUSE);
                break;
            case MAIN:
            case PAUSE:
                mode_state.changeMode(ModeState.Mode.WALK);
                break;
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClickLittleButton(View view) {
        switch (view.getId()){
            case R.id.button_switch_input:
                mode_state.checkoutLeaveDrawMode();
                button_switch_input.nextVariant();
                mode_state.checkoutDrawMode();
                break;
            case R.id.button_clear:
                ClearConfirmDialogFragment clear_confirm_dialog_fragment = new ClearConfirmDialogFragment(this);
                clear_confirm_dialog_fragment.show(main_activity.getSupportFragmentManager(),"ClearConfirmDialogFragment");
                break;
            case R.id.button_connection:
                routeSender.goToRouteSender();
                break;
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog.getTag() == null) {
            throw new RuntimeException("No dialog tag!");
        }
        switch (dialog.getTag()) {
            case "ClearConfirmDialogFragment":
                line_drawer.clear();
                break;
            case "LeaveWalkingConfirmation":
            case "LeavePauseConfirmation":
                mode_state.goToMainConfirmation(dialog.getTag());
                break;
            default:
                throw new RuntimeException("Unknown dialog");
        }
    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }


    @Override
    public void onTimeUpdated(Time time) {
        counter_monitor.setTopText(time.toString());
    }

    @Override
    public void onFinish() {
        System.out.println("finish");
        mode_state.changeMode(ModeState.Mode.FINISH);
    }



    public void onStop() {
        map_state.onStop();
    }

    public void onStart() {
        map_state.onStart(location_sensor);
    }

    public MapState getMapState(){
        return map_state;
    }

    public NavigatorState getNavigatorState(){
        return navigator_state;
    }
}
