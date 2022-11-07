package com.helkor.project.connection.websocket.util.serialized;

import java.util.ArrayList;
import java.util.List;

public class Point {
    private final double latitude;
    private final double longitude;

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public static List<Point> convertMapkitToPOJO(List<com.yandex.mapkit.geometry.Point> points){
        List<Point> pojoPoints = new ArrayList<>();
        for (com.yandex.mapkit.geometry.Point point : points) {
            pojoPoints.add(new Point(point.getLatitude(),point.getLongitude()));
        }
        return pojoPoints;
    }
    public static List<com.yandex.mapkit.geometry.Point> convertPOJOToMapkit(Point[] points){
        List<com.yandex.mapkit.geometry.Point> pojoPoints = new ArrayList<>();
        for (Point point : points) {
            pojoPoints.add(new com.yandex.mapkit.geometry.Point(point.getLatitude(),point.getLongitude()));
        }
        return pojoPoints;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
