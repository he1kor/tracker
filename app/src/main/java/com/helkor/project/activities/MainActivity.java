package com.helkor.project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.helkor.project.R;
import com.helkor.project.global.Controller;
import com.helkor.project.global.YandexMapkit;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {

    Controller controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKit map_kit = YandexMapkit.initAPI(this);
        setContentView(R.layout.activity_main);

        controller = new Controller(this,map_kit);
    }

    //MapKitFactory setup & stop
    @Override
    protected void onStop() {
        super.onStop();
        controller.onStop();
    }
    @Override
    protected void onStart() {
        super.onStart();
        controller.onStart();
    }

    //Navigator setup
    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        controller.getNavigatorState().init(userLocationView);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
        controller.getNavigatorState().update(userLocationView);
    }
}