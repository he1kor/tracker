package com.helkor.project.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.helkor.project.R;
import com.helkor.project.global.Controller;
import com.helkor.project.global.YandexMapkit;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;

public class MainActivity extends AppCompatActivity{

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
        controller.onStop();
        super.onStop();
    }
    @Override
    protected void onStart() {
        super.onStart();
        controller.onStart();
    }
}