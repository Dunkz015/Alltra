package com.app.alltra.app_usage;

import android.graphics.drawable.Drawable;

public class AppItem {
    private Drawable iconResource;
    private String name;
    private int progress;
    private double value;

    public AppItem(Drawable iconResource, String name, int progress, double value) {
        this.iconResource = iconResource;
        this.name = name;
        this.progress = progress;
        this.value = value;
    }

    public Drawable getIconResource() {
        return iconResource;
    }

    public void setIconResource(Drawable iconResource) {
        this.iconResource = iconResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
