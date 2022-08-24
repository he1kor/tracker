package com.helkor.project.draw.util;

import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RouteSegment {

    Point previous_point;
    Point last_point;
    double length;
    PolylineMapObject polyline_object;

    public RouteSegment (Point previous_point, Point last_point, MapObjectCollection polyline_collection){

        this.previous_point = previous_point;
        this.last_point = last_point;

        length = Geo.distance(this.previous_point,this.last_point);

        {
            ArrayList<Point> both_points = new ArrayList<>(Arrays.asList(previous_point,last_point));
            polyline_object = polyline_collection.addPolyline(new Polyline(both_points));
        }

    }

    public Point getPreviousPoint() {
        return previous_point;
    }

    public Point getLastPoint() {
        return last_point;
    }

    public double getLength() {
        return length;
    }

    public PolylineMapObject getPolylineObject() {
        return polyline_object;
    }

    public static Vector toLine(RouteSegment route_segment){
        Vector vector = new Vector(route_segment.previous_point, route_segment.last_point);
        return vector;
    }
}
