package com.richzjc.shortvideo.util;

import android.graphics.drawable.GradientDrawable;

public class ShapeDrawable {

    private int strokeWidth; // px not dp
    private int radius; // px not dp
    private int strokeColor;
    private int fillColor;
    private int padding;


    public ShapeDrawable(Build build) {
        this.strokeWidth = build.strokeWidth;
        this.radius = build.radius;
        this.strokeColor = build.strokeColor;
        this.fillColor = build.fillColor;
        this.padding = build.padding;
    }

    public GradientDrawable getDrawable() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(radius);
        gd.setStroke(strokeWidth, strokeColor);
        return gd;
    }

    public static GradientDrawable getDrawable(int strokeWidth, int radius, int strokeColor, int fillColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(radius);
        gd.setStroke(strokeWidth, strokeColor);
        return gd;
    }

    public static GradientDrawable getDrawable(int strokeWidth, float[] radius, int strokeColor, int fillColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadii(radius);
        gd.setStroke(strokeWidth, strokeColor);
        return gd;
    }

    /**
     * 横向渐变背景
     *
     * @param radius
     * @param colorStart
     * @param colorEnd
     * @return
     */
    public static GradientDrawable getLinearDrawable(int radius, int colorStart, int colorEnd) {
        return getLinearDrawable(radius, new int[]{colorStart, colorEnd}, GradientDrawable.Orientation.LEFT_RIGHT);
    }

    public static GradientDrawable getLinearDrawable(int radius, int colorStart, int colorEnd, GradientDrawable.Orientation orientation) {
        return getLinearDrawable(radius, new int[]{colorStart, colorEnd}, orientation);
    }

    public static GradientDrawable getLinearDrawable(int radius, int[] colors, GradientDrawable.Orientation orientation) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColors(colors);
        gd.setOrientation(orientation);
        if (radius > 0) {
            gd.setCornerRadius(radius);
        }
        return gd;
    }

    static class Build {
        private int strokeWidth; // px not dp
        private int radius; // px not dp
        private int strokeColor;
        private int fillColor;
        private int padding;

        public Build setStockWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Build setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public Build setStockColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public Build setFillColor(int fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public Build setPadding(int padding) {
            this.padding = padding;
            return this;
        }

        public ShapeDrawable build() {
            return new ShapeDrawable(this);
        }

    }


}
