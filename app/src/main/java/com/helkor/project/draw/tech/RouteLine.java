package com.helkor.project.draw.tech;

import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;

import java.util.ArrayList;

public class RouteLine {
    public Point getFirstPoint() {
        return first_point;
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

    Point first_point;
    Point last_point;
    double length;
    PolylineMapObject polyline_object;
    public RouteLine (Point first_point, Point last_point, MapObjectCollection polyline_collection){

        this.first_point = first_point;
        this.last_point = last_point;

        Polyline polyline;
        {
            ArrayList<Point> last_points = new ArrayList<Point>();

            last_points.add(first_point);
            last_points.add(last_point);
            polyline = new Polyline(last_points);
        }
        length = Geo.distance(first_point,last_point);

        polyline_object = polyline_collection.addPolyline(polyline);
    }

}
