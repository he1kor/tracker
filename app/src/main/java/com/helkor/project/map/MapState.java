package com.helkor.project.map;


import android.view.View;
import com.helkor.project.draw.LocationSensor;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.map.MapWindow;
import com.yandex.mapkit.mapview.MapView;

public class MapState {

    private MapView map_view;
    private MapWindow map_window;
    private Map map;
    private MapKit map_kit;
    private MapObjectCollection map_object_collection;


    public MapState(MapView map_view, MapKit map_kit){

        this.map_kit = map_kit;
        this.map_view = map_view;
        map_window = map_view.getMapWindow();
        map = map_view.getMap();
        map_object_collection = map.getMapObjects();
        init();
    }

    public void onStop() {
        map_view.onStop();
        map_kit.onStop();
        System.out.println("mapkit stopped");
    }
    public void onStart(LocationSensor location_sensor) {
        map_kit.onStart();
        map_view.onStart();
    }

    private void init(){
        map_view.getMap().setScrollGesturesEnabled(true);
        map_view.getMap().setZoomGesturesEnabled(true);
        map_view.getMap().setTiltGesturesEnabled(true);
        map_view.getMap().setMapType(MapType.VECTOR_MAP);
        map_view.getMap().setModelsEnabled(true);
        map_view.getMap().set2DMode(false);
    }

    public void show(){
        map_view.setVisibility(View.VISIBLE);
    }
    public void hide(){
        map_view.setVisibility(View.INVISIBLE);
    }
    public MapView getMapView() {
        return map_view;
    }
    public MapObjectCollection getMapObjects(){
        return map_object_collection;
    }

    public MapWindow getMapWindow() {
        return map_window;
    }
    public Map getMap(){
        return map;
    }
    public MapKit getMapKit() {
        return map_kit;
    }
}
