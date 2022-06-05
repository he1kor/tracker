package com.helkor.project.draw;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.helkor.project.R;
import com.helkor.project.draw.tech.RouteLine;
import com.helkor.project.global.Controller;
import com.helkor.project.map.MapState;
import com.helkor.project.map.tech.Coordinates;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.ColoredPolylineMapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectVisitor;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolygonMapObject;
import com.yandex.mapkit.map.PolylineMapObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LineDrawer {
    private final Controller controller;

    Activity activity;
    private PolylineMapObject polyline;

    ArrayList<RouteLine> route = new ArrayList<>();

    private MapObjectCollection map_object_collection;
    private Point last_point;
    private final int MAX_POINTS = 400;
    private final int MAX_DIVISION_LINE = 500;
    private final double MIN_UNSEEN_STEP = 0.00000001;
    private static final double WALKED_ACCURACY = 100;
    String test = "";
    private double division_step;
    private double min_real_step;
    private double travelled_path;
    boolean cross_pointed = true;

    private double path;
    private int travelled_index;
    private boolean is_counting = false;

    private boolean is_phantom_last = false;
    private MapState map_state;
    String text = "";

    @SuppressLint("HandlerLeak")
    public LineDrawer(Controller controller, MapState map_state) {
        this.controller = controller;
        activity = controller.getActivity();

        travelled_index = 0;
        travelled_path = 0;
        path = 0;
        this.map_state = map_state;
        map_object_collection = map_state.getMapView().getMap().getMapObjects().addCollection();
    }

    private void drawNew(Point first_point, Point last_point){

        path += Geo.distance(first_point,last_point);
        controller.updatePathValue(path);

        route.add(new RouteLine(first_point,last_point,map_object_collection));

        if (route.size() > MAX_POINTS) removePoint(0);
    }
    private void insertPoint(int inserting_index, Point point){

        removePoint(inserting_index);

        route.add(inserting_index,new RouteLine(route.get(inserting_index-1).getLastPoint(),point,map_object_collection));
        path += Geo.distance(point,route.get(inserting_index).getLastPoint());
        route.add(inserting_index+1,new RouteLine(point,route.get(inserting_index+1).getFirstPoint(),map_object_collection));
        path += Geo.distance(point,route.get(inserting_index+1).getLastPoint());
        controller.updatePathValue(path);
        if (route.size() > MAX_POINTS) removePoint(0);
    }
    private void join(int index){
        //joins element with the past one
        RouteLine first_line = new RouteLine(route.get(index-1).getFirstPoint(),route.get(index).getLastPoint(),map_object_collection);

        removePoint(index-1);
        removePoint(index-1);

        route.add(index-1,first_line);
        path += route.get(index-1).getLength();
        controller.updatePathValue(path);
        if (route.size() > MAX_POINTS) removePoint(0);
    }

    private void colorize(){
        double current_path = 0;

        //if (route.size() > 0) {
        //    for (int i = 0; i <= travelled_index; i++) {
        //        current_path += route.get(i).getLength();
        //        PolylineMapObject polyline_object = route.get(i).getPolylineObject();
        //        polyline_object.setStrokeColor(Color.argb((int) Math.round(100 + (100 * current_path / path)), 50, (int) Math.round(128 + (127 * current_path / path)), (int) Math.round(70 + (128 * current_path / path))));
        //    }
        //}
        //if (travelled_index < route.size()-1) {
        //    for (int i = travelled_index + 1; i < route.size(); i++) {
        //        current_path += route.get(i).getLength();
        //        PolylineMapObject polyline_object = route.get(i).getPolylineObject();
        //        polyline_object.setStrokeColor(Color.argb((int) Math.round(100 + (100 * current_path / path)), (int) Math.round(255 * current_path / path), 5, 155));
        //    }
        //}
        for (int i = 0; i < route.size(); i++){
            PolylineMapObject polyline_object = route.get(i).getPolylineObject();
            if (i % 2 == 0) polyline_object.setStrokeColor(Color.argb(255,0,0,0));
            else polyline_object.setStrokeColor(Color.argb(255,255,0,255));
        }
    }

    private void removePoint(int index){
        map_object_collection.remove(route.get(index).getPolylineObject());
        path -= route.get(index).getLength();
        route.remove(index);

        if (route.size() > 0) last_point = route.get(route.size()-1).getFirstPoint();
        else last_point = null;
    }

    public void addPoint(Point point){
        colorize();
        double distance;
        if (is_phantom_last) removePoint(route.size()-1);
        is_phantom_last = false;
        is_counting = true;
        if (this.last_point != null) {
            distance = Geo.distance(point,this.last_point);
            double first_longitude = this.last_point.getLongitude();
            double first_latitude = this.last_point.getLatitude();

            double second_longitude = point.getLongitude();
            double second_latitude = point.getLatitude();

            double longitude_signed_distance = second_longitude - first_longitude;
            double latitude_signed_distance = second_latitude - first_latitude;

            double iterations = distance / division_step;

            double longitude_step = longitude_signed_distance / iterations;
            double latitude_step = latitude_signed_distance / iterations;
            if (iterations > 1) {

                for (int i = 0; i < Math.floor(iterations) && i < MAX_DIVISION_LINE / division_step; i++) {
                    Point past_point = this.last_point;
                    this.last_point = new Point(past_point.getLatitude() + latitude_step, past_point.getLongitude() + longitude_step);
                    drawNew(past_point,last_point);
                }

            }
            else if (distance > min_real_step) {
                Point past_point = this.last_point;
                this.last_point = point;
                drawNew(past_point,last_point);
            }
        }
        else{
            Point past_point = new Point(point.getLatitude() + MIN_UNSEEN_STEP, point.getLongitude() + MIN_UNSEEN_STEP);
            this.last_point = point;
            drawNew(past_point,last_point);
        }
        colorize();
        is_counting = false;
    }
    public void addPhantomPoint(Point point){
        double distance;
        if (is_phantom_last) removePoint(route.size()-1);
        is_phantom_last = true;
        is_counting = true;
        if (this.last_point != null) {
            distance = Geo.distance(point,this.last_point);
            double first_longitude = this.last_point.getLongitude();
            double first_latitude = this.last_point.getLatitude();

            double second_longitude = point.getLongitude();
            double second_latitude = point.getLatitude();

            double longitude_signed_distance = second_longitude - first_longitude;
            double latitude_signed_distance = second_latitude - first_latitude;

            double iterations = distance / division_step;

            double longitude_step = longitude_signed_distance / iterations;
            double latitude_step = latitude_signed_distance / iterations;
            if (iterations > 1) {

                for (int i = 0; i < Math.floor(iterations) && i < MAX_DIVISION_LINE / division_step; i++) {
                    Point past_point = this.last_point;
                    this.last_point = new Point(past_point.getLatitude() + latitude_step, past_point.getLongitude() + longitude_step);
                    drawNew(past_point,point);
                }

            }
            else if (distance > min_real_step) {
                Point past_point = this.last_point;
                this.last_point = point;
                drawNew(past_point,last_point);
            }
        }
        else{
            Point past_point = new Point(point.getLatitude() + MIN_UNSEEN_STEP, point.getLongitude() + MIN_UNSEEN_STEP);
            this.last_point = point;
            drawNew(past_point,last_point);
        }
        colorize();
        is_counting = false;
    }

    public void checkForTravelled(Point point, double accuracy) {
        int possible_indexes_amount = 0;
        for (int i = travelled_index; i < route.size(); i++){
            possible_indexes_amount++;
            if (possible_indexes_amount > WALKED_ACCURACY) break;
        }
        possible_indexes_amount = 1000;
        for (int i = travelled_index; i < travelled_index + possible_indexes_amount && i < route.size(); i++) {
            if (Geo.distance(point,route.get(travelled_index).getFirstPoint()) < (WALKED_ACCURACY / 10) + (1.5 * accuracy)) {
                travelled_index = i;
                if (travelled_index > 0) {
                    if (cross_pointed) {
                        Toast toast = Toast.makeText(activity,
                                "crosspointed!", Toast.LENGTH_SHORT);
                        toast.show();
                        Point to_point = Coordinates.changeLength(new Point (route.get(travelled_index).getFirstPoint().getLatitude(),route.get(travelled_index).getFirstPoint().getLongitude()),route.get(travelled_index).getLastPoint(),division_step / 1000000);
                        insertPoint(travelled_index, to_point);
                        cross_pointed = false;
                    } else {
                        if (route.get(travelled_index).getLength() <= division_step / 10000000) {
                            //Toast toast = Toast.makeText(activity,
                            //        "join <= step!", Toast.LENGTH_SHORT);
                            //toast.show();
                            join(travelled_index);
                            travelled_index--;
                            i--;
                            cross_pointed = true;
                        } else if (travelled_index < route.size() - 1) {
                            Point to_point = Coordinates.changeLength(new Point (route.get(travelled_index).getFirstPoint().getLatitude(),route.get(travelled_index).getFirstPoint().getLongitude()),route.get(travelled_index).getLastPoint(),division_step / 1000000);

                            i--;
                            travelled_index--;
                            //Toast toast = Toast.makeText(activity,
                            //        "forward!", Toast.LENGTH_SHORT);
                            //toast.show();
                            insertPoint(travelled_index, to_point);
                            join(travelled_index);

                        }
                    }
                    colorize();
                }

                test = test + cross_pointed + " " + route.size() + " " + travelled_index + "\n\n";
                controller.test(test);
            }
        }
        travelled_path = 0;
        for (int i = 0; i < travelled_index; i++){
            travelled_path += route.get(i).getLength();
        }
        if (travelled_path == path) controller.setFinishedMode();
        controller.updatePathValue(path,travelled_path);


        colorize();
    }
    public void clear(){
        map_object_collection.clear();
        map_object_collection = map_state.getMap().getMapObjects().addCollection();
        route.clear();
        path = 0;
        is_phantom_last = false;
        travelled_index = 0;
        travelled_path = 0;
        controller.updatePathValue(path);
        last_point = null;
        text = "";
    }
    public void resetWalkedPath(){
        travelled_index = 0;
        travelled_path = 0;
        colorize();
        controller.updatePathValue(path,travelled_path);

    }
    public boolean isCounting(){
        return is_counting;
    }
    public void setMinRealStep(double min_real_step){
        this.min_real_step = min_real_step;
    }

    public Point getLastPoint() {
        if (route.size() > 0) return route.get(route.size()-1).getLastPoint();
        else return null;
    }
    public double getPath() {
        return path;
    }

    public void setDivisionStep(double division_step) {
        this.division_step = division_step;
    }

}
