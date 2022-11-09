package com.helkor.project.draw.util;

import com.yandex.mapkit.geometry.Point;

public class TravalableRoute extends Route {

    private double travelled_path;
    private int travelled_index;


    private double intermediate_multiplier;
    private boolean is_last_segment_divided;

    private final double WALKED_ACCURACY = 100;
    private final double SUBSTEP_PERCENT = 0.02;

    public TravalableRoute(){
        super();
        travelled_path = 0;
        travelled_index = 0;
        intermediate_multiplier = 0;
        is_last_segment_divided = false;
    }

    private void removeDividedPoint(){
        routePoints.remove(travelled_index);
        intermediate_multiplier = 0;
        is_last_segment_divided = false;
    }

    private void travelWholeStep(){
        travelled_path += getDistance(travelled_index,travelled_index+1);
        if (is_last_segment_divided) {
            removeDividedPoint();
        } else {
            travelled_index++;
        }
    }
    private void travelSubStep(Point intermediate_point){
        travelled_path += getDistance(routePoints.get(travelled_index),intermediate_point);

        if (is_last_segment_divided) {
            routePoints.remove(travelled_index);
        } else {
            travelled_index++;
        }

        routePoints.add(travelled_index,intermediate_point);
        is_last_segment_divided = true;
    }


    public void checkWholeSteps(Point current_position, double checking_distance){
        int possible_indexes_amount = (int) Math.min(WALKED_ACCURACY, routePoints.size()-travelled_index-1);
        for (int i = 0; (i < possible_indexes_amount); i++) {
            if (getDistance(current_position, routePoints.get(travelled_index+1)) < checking_distance) {
                travelWholeStep();
            }
        }
    }
    public void checkSubSteps(Point current_position, double checking_distance){
        if (travelled_index == routePoints.size()-1) {
            return;
        }

        Point intermediate_point = new Vector(routePoints.get(travelled_index),routePoints.get(travelled_index+1)).getPointByMultiplier(intermediate_multiplier);
        boolean is_divided_segment_changed = false;
        for (;(getDistance(current_position, intermediate_point) < checking_distance) && (intermediate_multiplier < 1);
             intermediate_multiplier += SUBSTEP_PERCENT) {
            is_divided_segment_changed = true;
            intermediate_point = new Vector(routePoints.get(travelled_index),routePoints.get(travelled_index+1)).getPointByMultiplier(intermediate_multiplier);
        }
        if (is_divided_segment_changed){
            travelSubStep(intermediate_point);
        }
    }

    @Override
    public void clear(){
        super.clear();
        resetTravelledPath();
    }
    public void resetTravelledPath() {
        travelled_index = 0;
        travelled_path = 0;
        if (is_last_segment_divided) {
            routePoints.remove(travelled_index);
            is_last_segment_divided = false;
            intermediate_multiplier = 0;
        }
    }
    public double getTravelledPath() {
        return travelled_path;
    }
    public int getTravelledIndex() {
        return travelled_index;
    }
}
