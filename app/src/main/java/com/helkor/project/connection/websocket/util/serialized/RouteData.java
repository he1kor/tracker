package com.helkor.project.connection.websocket.util.serialized;

import java.util.List;

public class RouteData {
    private List<com.yandex.mapkit.geometry.Point> points;
    private String token;
    public RouteData(List<com.yandex.mapkit.geometry.Point> points, String token){
        this.points = points;
        this.token = token;
    }
}
