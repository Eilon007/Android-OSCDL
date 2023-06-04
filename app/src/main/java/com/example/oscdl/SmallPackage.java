package com.example.oscdl;

import android.graphics.Bitmap;

public class SmallPackage
{
    String appname;
    String version;
    String devname;
    String category;
    String desc;
    int size;
    Bitmap img;
    String zip_url;

    public SmallPackage(String appname, String version, String devname, String category, String desc, Bitmap img, int size, String zip_url) {
        this.appname = appname;
        this.version = version;
        this.devname = devname;
        this.category = category;
        this.desc = desc;
        this.img = img;
        this.size = size;
        this.zip_url = zip_url;
    }

    public SmallPackage() {
        this.appname = null;
        this.version = null;
        this.devname = null;
        this.category = null;
        this.desc = null;
        this.img = null;
    }



    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getZip_url() {
        return zip_url;
    }

    public void setZip_url(String zip_url) {
        this.zip_url = zip_url;
    }




}
