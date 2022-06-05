package com.helkor.project.map.tech;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;

public class ShortLocation {
    private double accuracy;
    private Point point;

    public ShortLocation(Location location){
        accuracy = location.getAccuracy();
        point = location.getPosition();
    }
    public Point getPoint(){
        return point;
    }
    public double getAccuracy(){
        return accuracy;
    }
    public double getLatitude(){
        return point.getLatitude();
    }
    public double getLongitude(){
        return point.getLongitude();
    }
}

