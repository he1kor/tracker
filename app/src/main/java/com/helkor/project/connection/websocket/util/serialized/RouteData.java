package com.helkor.project.connection.websocket.util.serialized;


import java.util.List;

public class RouteData {
    private List<Point> points;
    private List<Point> markPoints;
    private String token;
    public RouteData(List<Point> points, List<Point> markPoints,String token){
        this.points = points;
        this.markPoints = markPoints;
        this.token = token;
    }

    public List<Point> getPoints() {
        return points;
    }

    public List<Point> getMarkPoints() {
        return markPoints;
    }

    public String getToken() {
        return token;
    }
}
