package com.helkor.project.graphics;

public interface ColorVariable {
    public enum Variant{
        MAIN,
        DRAW,
        WALK,
        PAUSE,
        FINISH
    }
    void updateView();
    void setVariant(Variant variant);
    Variant getVariant();
}
