package com.giant.watsonapp.models;

import java.io.Serializable;

/**
 * Created by Jorble on 2017/8/28.
 * 用来设定传入地图的标注对象
 */

public class MyMarker implements Serializable{
    private String name;
    private int type;
    private String img;
    private String lat;
    private String lon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
