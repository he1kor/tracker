package com.helkor.project.util;

public class Bool {
    public static String toString(boolean bool){
        if (bool) return "True";
        else return "False";
    }
    public static int toInt(boolean bool){
        if (bool) return 1;
        else return 0;
    }
}
