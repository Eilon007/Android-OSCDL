package com.example.oscdl;

import android.graphics.Bitmap;

public class ThemeItem
{
    private String zipFilePath;
    private String name;
    private Bitmap icon;

    public ThemeItem(String zipFilePath, String name, Bitmap icon) {
        this.zipFilePath = zipFilePath;
        this.name = name;
        this.icon = icon;
    }

    public String getZipFilePath() {
        return zipFilePath;
    }

    public String getName() {
        return name;
    }

    public Bitmap getIcon() {
        return icon;
    }
}