package com.helkor.project.map;

import android.app.Activity;
import android.graphics.PointF;

import androidx.annotation.NonNull;

import com.helkor.project.R;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

public class NavigatorState implements UserLocationObjectListener{

    Activity activity;
    private UserLocationLayer user_location_layer;
    private MapKit map_kit;

    public NavigatorState(Activity activity, MapState map_state){

        this.activity = activity;

        map_kit = MapKitFactory.getInstance();
        user_location_layer = map_kit.createUserLocationLayer(map_state.getMapWindow());
        user_location_layer.setObjectListener(this);
        user_location_layer.setVisible(true);
    }

    public void init(UserLocationView userLocationView){

        CompositeIcon ArrowIcon = userLocationView.getArrow().useCompositeIcon();
        ArrowIcon.setIcon(
                "arrow",
                ImageProvider.fromResource(activity, R.drawable.navigation_arrow),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(1.5f)
        );
        userLocationView.getArrow().setOpacity(0.6f);

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(activity, R.drawable.navigation_point),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.75f)
        );
        userLocationView.getPin().setOpacity(0.6f);

        userLocationView.getAccuracyCircle().setFillColor(activity.getColor(R.color.lilac) & 0x3affffff);
    }

    public void update(UserLocationView userLocationView) {
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        this.init(userLocationView);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
        this.update(userLocationView);
    }
}
