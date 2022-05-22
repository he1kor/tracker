package com.helkor.project.tech.map;

import com.helkor.project.MainActivity;
import com.yandex.mapkit.geometry.Geo;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;

import java.util.ArrayList;

public class ShortLocationArray {

    private ArrayList<ShortLocation> short_location_array;
    private int MAX_CHECK_LINE;
    private int size;
    private double max_distance;
    public ShortLocationArray(int MAX_CHECK_LINE){
        size = 0;
        max_distance = 0;
        this.MAX_CHECK_LINE = MAX_CHECK_LINE;
        short_location_array = new ArrayList<>();
    }
    public void add(Location location){
        if (short_location_array.size() >= MAX_CHECK_LINE){
            short_location_array.remove(0);
        }
        else size++;
        short_location_array.add(new ShortLocation(location));
        max_distance = 0;

        for (int first_index = 0; first_index < size-1; first_index++) {
            for (int second_index = first_index+1; second_index < size; second_index++) {

                double distance = Geo.distance(short_location_array.get(first_index).getPoint(),short_location_array.get(second_index).getPoint())
                        - short_location_array.get(first_index).getAccuracy()
                        - short_location_array.get(second_index).getAccuracy();

                if (distance >= max_distance) max_distance = distance;
                MainActivity.test_text.setText("Accuracy: " + getLastAccuracy() + "\nDistance: " + distance + "\nMaxDistance: " + max_distance);
            }
        }
    }
    public void clear(){
        short_location_array.clear();
        size = 0;
        max_distance = 0;
    }
    public double getLastLatitude(){
        return short_location_array.get(size-1).getLatitude();
    }
    public double getLastLongitude(){
        return short_location_array.get(size-1).getLongitude();
    }
    public int size(){
        return size;
    }
    public double getMaxDistance(){
        return max_distance;
    }

    public double getLastAccuracy() {
        return short_location_array.get(size-1).getAccuracy();
    }
    public Point getLastPoint(){
        return short_location_array.get(size-1).getPoint();
    }
}
