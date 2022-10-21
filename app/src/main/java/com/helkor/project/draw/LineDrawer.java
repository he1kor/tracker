package com.helkor.project.draw;

import android.annotation.SuppressLint;

import com.helkor.project.draw.util.Palette;
import com.helkor.project.global.Controller;
import com.helkor.project.map.MapState;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;

import java.util.ArrayList;

public class LineDrawer {

    private final Controller controller;

    private final MapObjectCollection map_object_collection;
    private PolylineMapObject routePolyline;
    private final Route route;
    private boolean is_temporary_last;

    private final double MIN_ADDITIONAL_ACCURACY = 4;
    private final int MAX_POINTS = 500;

    @SuppressLint("HandlerLeak")
    public LineDrawer(Controller controller, MapState map_state) {
        this.controller = controller;
        map_object_collection = map_state.getMapView().getMap().getMapObjects().addCollection();
        route = new Route();
        is_temporary_last = false;
    }
    private void checkIfFinished(){
        if (route.getTravelledPath() == route.getPath()){
            controller.setFinishedMode();
        }
    }
    private void update(){
        route.removeOverlimitedPoints(MAX_POINTS);
        updateViewValues();
        if (route.size() > 0) {
            if (routePolyline != null){
                routePolyline.setGeometry(new Polyline(route.getPoints()));
            } else {
                routePolyline = map_object_collection.addPolyline(new Polyline(route.getPoints()));
                Palette.configure(routePolyline,getMaxSegments());
            }
        }
        colorize();
    }

    private int getMaxSegments() {
        return MAX_POINTS + 1;
    }

    private void updateViewValues(){
        controller.updatePathValue(route.getPath(),route.getTravelledPath());
    }

    private void colorize(){
        if (route.size() == 0){
            return;
        }
        double path = route.getPath();
        int travelled_index = route.getTravelledIndex();
        double current_path = 0;
        int size = route.size();

        ArrayList<Integer> colorIndexes = new ArrayList<>();
        for (int i = 1; i <= travelled_index; i++) {
            current_path += route.getDistance(i-1,i);
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_GREEN,getMaxSegments()));
        }
        for (int i = travelled_index + 1; i < size; i++) {
            current_path += route.getDistance(i-1,i);
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_RED,getMaxSegments()));
        }
        routePolyline.setStrokeColors(colorIndexes);
    }

    private void tryRemoveTemporaryPoint(){
        if (is_temporary_last) {
            route.removeFrontPoint();
        }
    }
    public void buildToPoint(Point point, boolean is_temporary){
        this.is_temporary_last = is_temporary;
        route.buildToPoint(point);
        update();
    }
    public void buildToPoint(Point point){
        buildToPoint(point,false);
    }

    public void checkForTravelled(Point current_position, double accuracy) {
        if (route.size() > 0) {
            double checking_distance = accuracy + MIN_ADDITIONAL_ACCURACY;
            route.checkWholeSteps(current_position, checking_distance);
            route.checkSubSteps(current_position, checking_distance);
        }
        update();
        checkIfFinished();
    }


    public void clear(){
        map_object_collection.clear();
        routePolyline = null;
        route.clear();
        update();
    }
    public void resetTravelledPath(boolean requiredUpdate){
        route.resetTravelledPath();
        if (requiredUpdate){
            update();
        }
    }

    public Route getRoute(){
        return route;
    }
    public void resetTravelledPath(){
        resetTravelledPath(true);
    }

    public void setDivisionStep(int division_step) {
        route.setDivisionStep(division_step,MAX_POINTS);
    }
}
