package com.helkor.project.graphics;

public interface ColorVariable {
    enum Variant{
        MAIN,
        DRAW,
        VIEW,
        WALK,
        PAUSE,
        FINISH
    }
    void updateView();
    void setVariant(Variant variant);
    Variant getVariant();
}
