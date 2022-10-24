package com.helkor.project.monitors.util;

public abstract class TwoLinesText {
    protected String top_text;
    protected String bottom_text;
    public void setTopText(String text){
        top_text = text;
    }
    public void setBottomText(String text){
        bottom_text = text;
    };
    public String getTopText(){
        return top_text;
    }
    public String getBottomText(){
        return bottom_text;
    }
}
