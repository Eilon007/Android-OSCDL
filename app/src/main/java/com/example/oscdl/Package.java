package com.example.oscdl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class Package
{
    private String category;
    private String coder;
    private String contributors;
    private String controllers;
    private String display_name;
    private int downloads;
    private ArrayList<String> extra_directories;
    private int extracted;
    private String icon_url;
    private String internal_name;
    private String long_description;
    private String package_type;
    private String rating;
    private int release_date;
    private String shop_title_id;
    private Object shop_title_version;
    private String short_description;
    private int updated;
    private String version;
    private int zip_size;
    private String zip_url;

    public Package(String category, String coder, String contributors, String controllers, String display_name, int downloads, ArrayList<String> extra_directories, int extracted, String icon_url, String internal_name, String long_description, String package_type, String rating, int release_date, String shop_title_id, Object shop_title_version, String short_description, int updated, String version, int zip_size, String zip_url) {
        this.category = category;
        this.coder = coder;
        this.contributors = contributors;
        this.controllers = controllers;
        this.display_name = display_name;
        this.downloads = downloads;
        this.extra_directories = extra_directories;
        this.extracted = extracted;
        this.icon_url = icon_url;
        this.internal_name = internal_name;
        this.long_description = long_description;
        this.package_type = package_type;
        this.rating = rating;
        this.release_date = release_date;
        this.shop_title_id = shop_title_id;
        this.shop_title_version = shop_title_version;
        this.short_description = short_description;
        this.updated = updated;
        this.version = version;
        this.zip_size = zip_size;
        this.zip_url = zip_url;
    }

    public Package()
    {

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoder() {
        return coder;
    }

    public void setCoder(String coder) {
        this.coder = coder;
    }

    public String getContributors() {
        return contributors;
    }

    public void setContributors(String contributors) {
        this.contributors = contributors;
    }

    public String getControllers() {
        return controllers;
    }

    public void setControllers(String controllers) {
        this.controllers = controllers;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public ArrayList<String> getExtra_directories() {
        return extra_directories;
    }

    public void setExtra_directories(ArrayList<String> extra_directories) {
        this.extra_directories = extra_directories;
    }

    public int getExtracted() {
        return extracted;
    }

    public void setExtracted(int extracted) {
        this.extracted = extracted;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getInternal_name() {
        return internal_name;
    }

    public void setInternal_name(String internal_name) {
        this.internal_name = internal_name;
    }

    public String getLong_description() {
        return long_description;
    }

    public void setLong_description(String long_description) {
        this.long_description = long_description;
    }

    public String getPackage_type() {
        return package_type;
    }

    public void setPackage_type(String package_type) {
        this.package_type = package_type;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getRelease_date() {
        return release_date;
    }

    public void setRelease_date(int release_date) {
        this.release_date = release_date;
    }

    public String getShop_title_id() {
        return shop_title_id;
    }

    public void setShop_title_id(String shop_title_id) {
        this.shop_title_id = shop_title_id;
    }

    public Object getShop_title_version() {
        return shop_title_version;
    }

    public void setShop_title_version(Object shop_title_version) {
        this.shop_title_version = shop_title_version;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getZip_size() {
        return zip_size;
    }

    public void setZip_size(int zip_size) {
        this.zip_size = zip_size;
    }

    public String getZip_url() {
        return zip_url;
    }

    public void setZip_url(String zip_url) {
        this.zip_url = zip_url;
    }
}
