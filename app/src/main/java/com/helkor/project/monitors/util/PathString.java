package com.helkor.project.monitors.util;

import com.helkor.project.draw.LineDrawer;

public class PathString implements LineDrawer.PathListener{
    public final static boolean TOP = true;
    public final static boolean BOTTOM = false;
    private int path;
    private int travelledPath;

    private boolean travelledPathEnabled;

    private final boolean ROW;

    private final TwoLinesText twoLinesText;

    public PathString(TwoLinesText twoLinesText,boolean row){
        this.twoLinesText = twoLinesText;
        this.ROW = row;
        path = 0;
        travelledPath = 0;
        travelledPathEnabled = false;
        updatePathText();
    }

    public void setTravelledPathEnabled(boolean travelledPathEnabled) {
        this.travelledPathEnabled = travelledPathEnabled;
        updatePathText();
    }

    public boolean isTravelledPathEnabled() {
        return travelledPathEnabled;
    }

    private void setPath(int path){
        this.path = path;
        updatePathText();
    }
    private void setTravelledPath(int travelledPath) {
        this.travelledPath = travelledPath;
        updatePathText();
    }
    private void setText(String text, boolean isTop){
        if (isTop == TOP){
            twoLinesText.setTopText(text);
        } else if (isTop == BOTTOM){
            twoLinesText.setBottomText(text);
        }
    }
    private void updatePathText(boolean isTop){
        if (travelledPathEnabled){
            setText(travelledPath + " / " + path + " m.",isTop);
        } else{
            setText(path + " m.",isTop);
        }
    }
    private void updatePathText(){
        if (travelledPathEnabled){
            setText(travelledPath + " / " + path + " m.",ROW);
        } else{
            setText(path + " m.",ROW);
        }
    }

    @Override
    public void onPathUpdated(int path) {
        setPath(path);
    }

    @Override
    public void onTravelPathUpdated(int travelledPath) {
        setTravelledPath(travelledPath);
    }

}
