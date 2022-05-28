package com.helkor.project.global;

import android.app.Activity;

import com.helkor.project.BuildConfig;
import com.helkor.project.R;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;

public class YandexMapkit {

    public static MapKit initAPI(Activity activity) {
        String MAPKIT_KEY = BuildConfig.API_KEY;
        MapKitFactory.setApiKey(MAPKIT_KEY);
        MapKitFactory.initialize(activity);
        return MapKitFactory.getInstance();
    }
}