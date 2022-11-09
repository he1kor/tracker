package com.helkor.project.draw;

import android.annotation.SuppressLint;

import com.helkor.project.draw.util.Palette;
import com.helkor.project.draw.util.Route;
import com.helkor.project.draw.util.TravalableRoute;
import com.helkor.project.map.MapState;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;

import java.util.ArrayList;
import java.util.List;

public class LineDrawer implements LocationSensor.UpdateListener,TouchSensor.OnTouchListener {

    private enum TravelModes{
        fill,
        parallel
    }
    private final MapObjectCollection mapObjectCollection;
    private PolylineMapObject mainRoutePolyline;
    private PolylineMapObject travelledRoutePolyline;
    private final TravalableRoute mainRoute;
    private final Route travelledRoute;
    private boolean isTemporaryLast;
    private TravelModes travelMode;
    private boolean isDrawable;

    private final TravelModes DEFAULT_TRAVEL_MODE = TravelModes.fill;
    private final double MIN_ADDITIONAL_ACCURACY = 4;
    private final int MAX_POINTS = 500;

    @Override
    public void onNewPoint(Point point, boolean isTemp) {
        buildToPoint(point,isTemp);
    }

    @Override
    public void onPointTravelled(Point point, double accuracy) {
        checkForTravelled(point,accuracy);
    }



    @Override
    public void onTouch(Point point, boolean isTemp) {
        if (!isDrawable) return;
        buildToPoint(point,isTemp);
    }

    public void switchTravelModes() {
        if (travelMode == TravelModes.fill){
            travelMode = TravelModes.parallel;
        } else {
            travelMode = TravelModes.fill;
        }
        checkOutTravelMode();
    }

    public interface PathListener{
        void onPathUpdated(int path);
        void onTravelPathUpdated(int travelled_path);
    }
    public interface FinishListener{
        void onFinish();
    }
    PathListener path_listener;
    FinishListener finish_listener;

    @SuppressLint("HandlerLeak")
    public LineDrawer(MapState map_state) {
        mapObjectCollection = map_state.getMapView().getMap().getMapObjects().addCollection();
        mainRoute = new TravalableRoute();
        travelledRoute = new Route();
        isTemporaryLast = false;
        isDrawable = true;
        travelMode = DEFAULT_TRAVEL_MODE;
    }
    public void setPathListener(Object implementation_context){
        try{
            path_listener = (LineDrawer.PathListener) implementation_context;
        } catch (Exception e){
            throw new RuntimeException(implementation_context.toString()
                    + " must implement PathListener");
        }
    }
    public void setFinishListener(Object implementation_context){
        try{
            finish_listener = (LineDrawer.FinishListener) implementation_context;
        } catch (Exception e){
            throw new RuntimeException(implementation_context.toString()
                    + " must implement FinishListener");
        }
    }
    private void checkIfFinished(){
        if (mainRoute.getTravelledIndex() == mainRoute.size()-1 || mainRoute.size() == 0){
            finish_listener.onFinish();
        }
    }
    private void checkOutTravelMode(){
        if (travelledRoutePolyline == null) {
            return;
        }
        switch (travelMode){
            case fill:
                travelledRoutePolyline.setVisible(false);
                break;
            case parallel:
                travelledRoutePolyline.setVisible(true);
                break;
        }
    }
    private void update(){
        mainRoute.removeOverlimitedPoints(MAX_POINTS);
        travelledRoute.removeOverlimitedPoints(MAX_POINTS * 4);
        path_listener.onPathUpdated((int) mainRoute.getPath());
        path_listener.onTravelPathUpdated((int) mainRoute.getTravelledPath());
        if (mainRoute.size() > 0) {
            if (mainRoutePolyline != null){
                mainRoutePolyline.setGeometry(new Polyline(mainRoute.getPoints()));
            } else {
                mainRoutePolyline = mapObjectCollection.addPolyline(new Polyline(mainRoute.getPoints()));
                Palette.configure(mainRoutePolyline,getMaxSegments());
            }
        }
        if (travelledRoute.size() > 0) {
            if (travelledRoutePolyline != null) {
                travelledRoutePolyline.setGeometry(new Polyline(travelledRoute.getPoints()));
                checkOutTravelMode();
            } else {
                travelledRoutePolyline = mapObjectCollection.addPolyline(new Polyline(travelledRoute.getPoints()));
                Palette.configure(travelledRoutePolyline, getMaxSegments());
            }
        }
        colorize();
    }

    private int getMaxSegments() {
        return MAX_POINTS - 1;
    }

    private void colorize(){
        if (mainRoute.size() == 0){
            return;
        }
        double path = mainRoute.getPath();
        int travelled_index = mainRoute.getTravelledIndex();
        double current_path = 0;
        int size = mainRoute.size();

        ArrayList<Integer> colorIndexes = new ArrayList<>();
        for (int i = 1; i <= travelled_index; i++) {
            current_path += mainRoute.getDistance(i-1,i);
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_GREEN,getMaxSegments()));
        }
        for (int i = travelled_index + 1; i < size; i++) {
            current_path += mainRoute.getDistance(i-1,i);
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_RED,getMaxSegments()));
        }
        System.out.println(colorIndexes);
        mainRoutePolyline.setStrokeColors(colorIndexes);

        if (travelledRoutePolyline == null){
            return;
        }
        colorIndexes.clear();
        path = travelledRoute.getPath();
        current_path = 0;
        size = travelledRoute.size();

        colorIndexes = new ArrayList<>();
        for (int i = 1; i < size; i++){
            current_path += travelledRoute.getDistance(i-1,i);
            colorIndexes.add(Palette.percentToIndex(current_path/path,Palette.PALETTE_YELLOW,getMaxSegments()));
        }
        System.out.println(colorIndexes);
        travelledRoutePolyline.setStrokeColors(colorIndexes);
    }

    private void tryRemoveTemporaryPoint(){
        if (isTemporaryLast) {
            mainRoute.removeFrontPoint();
        }
    }
    public void buildToPoint(Point point, boolean is_temporary){
        this.isTemporaryLast = is_temporary;
        mainRoute.buildToPoint(point,true);
        update();
    }
    public void buildToPoint(Point point){
        buildToPoint(point,false);
    }
    public void createRoute(List<Point> points){
        mainRoute.clear();
        mainRoute.setRoutePoints(points);
        update();
    }

    public void checkForTravelled(Point current_position, double accuracy) {
        if (mainRoute.size() > 0) {
            double checking_distance = accuracy + MIN_ADDITIONAL_ACCURACY;
            mainRoute.checkWholeSteps(current_position, checking_distance);
            mainRoute.checkSubSteps(current_position, checking_distance);
            travelledRoute.buildToPoint(current_position,false);
        }
        update();
        checkIfFinished();
    }


    public void clear(){
        mapObjectCollection.clear();
        mainRoutePolyline = null;
        mainRoute.clear();
        travelledRoutePolyline = null;
        travelledRoute.clear();
        update();
    }
    public void resetTravelledPath(boolean requiredUpdate){
        mainRoute.resetTravelledPath();
        travelledRoute.clear();
        if (travelledRoutePolyline != null){
            mapObjectCollection.remove(travelledRoutePolyline);
        }
        travelledRoutePolyline = null;
        if (requiredUpdate){
            update();
        }
    }

    public void resetTravelledPath(){
        resetTravelledPath(true);
    }
    public Route getMainRoute(){
        return mainRoute;
    }

    public void setDivisionStep(int division_step) {
        mainRoute.setDivisionStep(division_step,MAX_POINTS);
        travelledRoute.setDivisionStep(division_step/2,MAX_POINTS*4);
    }
    public boolean isDrawable() {
        return isDrawable;
    }

    public void setDrawable(boolean drawable) {
        isDrawable = drawable;
    }
}
