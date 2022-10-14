package com.helkor.project.draw.util;

import android.graphics.Color;

import com.yandex.mapkit.map.PolylineMapObject;

public class Palette {
    private static final int SUB_PALETTES_AMOUNT = 2;
    public static final int PALETTE_RED = 1;
    public static final int PALETTE_GREEN = 0;

    public static void configure(PolylineMapObject polyline,int resolution){
        for (int subPaletteIndex = 0; subPaletteIndex < SUB_PALETTES_AMOUNT; subPaletteIndex++) {
            for (int i = 0; i < resolution; i++) {
                setPaletteColor(polyline,subPaletteIndex,i,resolution);
            }
        }
    }
    public static void setPaletteColor(PolylineMapObject polyline, int subPaletteIndex, int index, int resolution) {
        polyline.setPaletteColor(index + (resolution * subPaletteIndex), getSubColor(subPaletteIndex,index, resolution));
    }
    public static int percentToIndex(double percent, int subPaletteIndex, int resolution){
        return (int) (subPaletteIndex * resolution + Math.round(percent * (resolution-1)));
    }
    private static int getSubColor(int subPaletteIndex,int i, int resolution){
        if (subPaletteIndex == 0) return getGreenSubColor(i,resolution);
        else if (subPaletteIndex == 1) return getRedSubColor(i,resolution);
        return 0;
    }

    private static int getGreenSubColor(int i,int resolution){
        //0 index
        return Color.argb(
                (int) Math.round(100 + (100 * (double) i / resolution)),
                50,
                (int) Math.round(128 + (127 * (double) i / resolution)),
                (int) Math.round(70 + (128 * (double) i / resolution)));
    }

    private static int getRedSubColor(int i, int resolution) {
        //1 index
        return Color.argb(
                (int) Math.round(100 + (100 * (double) i / resolution)),
                (int) Math.round(255 * (double) i / resolution),
                5,
                155);
    }
}
