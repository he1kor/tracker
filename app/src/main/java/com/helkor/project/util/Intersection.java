package com.helkor.project.util;

import android.util.Pair;

import com.helkor.project.draw.util.RouteSegment;

import java.util.ArrayList;

public class Intersection {
    public static ArrayList<RouteSegment> ofArrays(ArrayList<RouteSegment> a, ArrayList<RouteSegment> b){
        ArrayList<RouteSegment> a_without_b = new ArrayList<>(a);
        a_without_b.removeAll(b);

        ArrayList<RouteSegment> b_without_a = new ArrayList<>(b);
        b_without_a.removeAll(a);

        ArrayList<RouteSegment> difference = new ArrayList<>();
        difference.addAll(b_without_a);
        difference.addAll(a_without_b);

        ArrayList<RouteSegment> result = new ArrayList<>(a);
        result.removeAll(difference);
        return result;
    }
    public static Pair<ArrayList<RouteSegment>,ArrayList<RouteSegment>> removeFromArrays(ArrayList<RouteSegment> a, ArrayList<RouteSegment> b){
        ArrayList<RouteSegment> difference = Intersection.ofArrays(a,b);
        a.removeAll(difference);
        b.removeAll(difference);
        return Pair.create(a,b);
    }
}
