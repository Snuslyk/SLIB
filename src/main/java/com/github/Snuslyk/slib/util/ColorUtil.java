package com.github.Snuslyk.slib.util;

import javafx.scene.paint.Color;

public class ColorUtil {

    public static int colorToNum(Color color){
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return  (red & 0xFF) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 16);
    }

    public static Color fromNum(int num) {
        if (num == 0) return null;
        return Color.rgb(num & 0xFF, (num >> 8) & 0xFF,(num >> 16) & 0xFF);
    }
}
