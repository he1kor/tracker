package com.helkor.project.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.helkor.project.R;
import com.helkor.project.activities.util.LocationPermissionResult;
import com.helkor.project.dialogs.AskForSettingsDialogFragment;
import com.helkor.project.global.Controller;
import com.helkor.project.global.YandexMapkit;
import com.yandex.mapkit.MapKit;

public class MainActivity extends AppCompatActivity implements AskForSettingsDialogFragment.AskForSettingsDialogListener {

    private Controller controller;
    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private LocationPermissionResult locationPermissionResult;
    boolean isWaitingSettingsPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("on create");
        MapKit map_kit = YandexMapkit.initAPI(this);
        setContentView(R.layout.activity_main);
        controller = new Controller(this, map_kit);
        initializeLocationPermissionRequest();
        checkLocationPermission();
    }
    private void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            isWaitingSettingsPermission = false;
            System.out.println("setup");
            controller.setup();

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showAskForSettingsDialog();
        } else {
            launchLocationPermissionRequest();
        }
    }
    private void initializeLocationPermissionRequest(){
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                    .RequestMultiplePermissions(), result -> {
                System.out.println("asked");
                Boolean fineLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (fineLocationGranted != null && fineLocationGranted) {
                    System.out.println("setup after ask");
                    controller.setup();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    showAskForSettingsDialog();
                } else {
                    showAskForSettingsDialog();
                }
            }
        );
    }
    public void showAskForSettingsDialog() {
        DialogFragment dialog = new AskForSettingsDialogFragment();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "AskForSettingsDialogFragment");
    }
    public void launchLocationPermissionRequest(){
        System.out.println("asking");
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
        System.out.println("after asking");
    }
    public LocationPermissionResult getLocationPermissionResult(){
        return locationPermissionResult;
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
        if (isWaitingSettingsPermission){
            checkLocationPermission();
        }
        controller.onStart();
    }
    private void goToSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(),null);
        intent.setData(uri);
        isWaitingSettingsPermission = true;
        startActivity(intent);
    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof AskForSettingsDialogFragment){
            goToSettings();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (dialog instanceof AskForSettingsDialogFragment){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}