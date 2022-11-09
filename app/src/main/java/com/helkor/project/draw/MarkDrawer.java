package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;

import com.helkor.project.R;
import com.helkor.project.map.MapState;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.geometry.Segment;
import com.yandex.mapkit.geometry.Subpolyline;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.runtime.ui_view.ViewProvider;

import java.util.ArrayList;
import java.util.List;

public class MarkDrawer implements TouchSensor.OnClickListener,TouchSensor.OnTouchListener{
    Activity activity;
    MapObjectCollection mapObjectCollection;
    List<PlacemarkMapObject> placemarkMapObjectList;
    LineDrawer lineDrawer;
    private boolean isLastMoving;
    private boolean isRemovedLast;
    private boolean isDrawable;

    public MarkDrawer(MapState mapState, Activity activity,LineDrawer lineDrawer){
        this.activity = activity;
        this.lineDrawer = lineDrawer;
        placemarkMapObjectList = new ArrayList<>();
        mapObjectCollection = mapState.getMapObjects().addCollection();
        isLastMoving = false;
        isRemovedLast = false;
        isDrawable = false;
    }
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    public void addItemMark(Location location){
        addItemMark(location.getPosition());
        if (location.getHeading() != null){
            placemarkMapObjectList.get(placemarkMapObjectList.size()-1).setDirection(location.getHeading().floatValue()+45);
        }
    }
    public void addItemMark(Point point){
        View view = new View(activity);
        view.setBackground(activity.getDrawable(R.drawable.plus));
        placemarkMapObjectList.add(mapObjectCollection.addPlacemark(point, new ViewProvider(view)));
        PlacemarkMapObject placemarkMapObject = placemarkMapObjectList.get(placemarkMapObjectList.size()-1);
        configurePlacemark(placemarkMapObject);
    }
    private double findClosestLineAngle(Point point){
        List<Point> points = lineDrawer.getMainRoute().getPoints();
        if (points.size() < 2) return 0;
        Point closestPoint = Geo.closestPoint(point, new Segment(points.get(0),points.get(1)));
        double minimalDistance = Geo.distance(closestPoint,point);
        Point minimalClosestPoint = closestPoint;
        for (int i = 2; i < points.size(); i++){
            closestPoint = Geo.closestPoint(point, new Segment(points.get(i-1),points.get(i)));
            double newDistance = Geo.distance(closestPoint,point);
            if (newDistance < minimalDistance){
                minimalDistance = newDistance;
                minimalClosestPoint = closestPoint;
            }

        }
        return Geo.course(point,minimalClosestPoint);
    }
    public void clear(){
        mapObjectCollection.clear();
        placemarkMapObjectList.clear();
    }
    public void setPoints(List<Point> points){
        clear();
        for (Point point : points) {
            addItemMark(point);
        }
    }
    public boolean tryRemovePlacemark(Point point){
        for (PlacemarkMapObject placemarkMapObject : placemarkMapObjectList) {
            if (Geo.distance(point,placemarkMapObject.getGeometry()) <= 5){
                mapObjectCollection.remove(placemarkMapObject);
                placemarkMapObjectList.remove(placemarkMapObject);
                return true;
            }
        }
        return false;
    }
    private void configurePlacemark(PlacemarkMapObject placemarkMapObject){
        placemarkMapObject.setOpacity(0.7f);
        placemarkMapObject.setDirection((float) findClosestLineAngle(placemarkMapObject.getGeometry()) + 45);
        placemarkMapObject.setIconStyle(new IconStyle()
                .setRotationType(RotationType.ROTATE)
                .setScale(0.5f));
    }

    @Override
    public void onTouch(Point point, boolean isTemp) {
        if (!isDrawable) return;
        if (isRemovedLast) return;
        if (!isLastMoving){
            if (tryRemovePlacemark(point)) {
                isRemovedLast = true;
                return;
            }
            addItemMark(point);
            isLastMoving = true;
            return;
        }
        PlacemarkMapObject placemarkMapObject = placemarkMapObjectList.get(placemarkMapObjectList.size()-1);
        placemarkMapObject.setGeometry(point);
        placemarkMapObject.setDirection((float) findClosestLineAngle(placemarkMapObject.getGeometry()) + 45);
    }
    @Override
    public void onClick(Point point) {
        if (!isDrawable) return;
        isLastMoving = false;
        isRemovedLast = false;
    }

    public boolean isDrawable() {
        return isDrawable;
    }

    public void setDrawable(boolean drawable) {
        isDrawable = drawable;
    }
    public List<Point> getMarkPoints(){
        List<Point> points = new ArrayList<>();
        for (PlacemarkMapObject placemarkMapObject : placemarkMapObjectList) {
            points.add(placemarkMapObject.getGeometry());
        }
        return points;
    }
}
