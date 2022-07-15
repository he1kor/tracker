package com.helkor.project.map.util;

import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;

public class Coordinates {
    public static double SignedLatitudeLength(Point first_point, Point last_point){
        return first_point.getLatitude() - last_point.getLatitude();
    }
    public static double SignedLongitudeLength(Point first_point, Point last_point){
        return first_point.getLongitude() - last_point.getLongitude();
    }
    public static double LatitudeLength(Point first_point, Point last_point){
        return Math.abs(first_point.getLatitude() - last_point.getLatitude());
    }
    public static double LongitudeLength(Point first_point, Point last_point){
        return Math.abs(first_point.getLongitude() - last_point.getLongitude());
    }
    public static double RatioFromLatitude(Point first_point, Point last_point){
        return LatitudeLength(first_point,last_point) / LongitudeLength(first_point,last_point);
    }
    public static double RatioFromLongitude(Point first_point, Point last_point){
        return LongitudeLength(first_point,last_point) / LatitudeLength(first_point,last_point);
    }
    //returns changed last point relative to the length of the vector and factor
    public static Point multiplyVector(Point first_point, Point last_point, double factor){
        return new Point(
                first_point.getLatitude() + (SignedLatitudeLength(first_point,last_point) * factor),
                first_point.getLongitude() + (SignedLongitudeLength(first_point,last_point) * factor)
                );
    }
    //returns new last point so that vector length is [length]
    public static Point changeLength(Point first_point, Point last_point, double length){
        double ratio = length / Geo.distance(first_point,last_point);
        return new Point(first_point.getLatitude() + SignedLatitudeLength(first_point,last_point) * ratio,
                first_point.getLongitude() + SignedLongitudeLength(first_point,last_point) * ratio);
    }
}
