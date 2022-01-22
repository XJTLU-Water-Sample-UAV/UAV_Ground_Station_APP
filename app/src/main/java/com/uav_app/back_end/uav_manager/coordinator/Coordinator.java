package com.uav_app.back_end.uav_manager.coordinator;

import androidx.annotation.NonNull;

public class Coordinator {
    private double lng;
    private double lat;
    private int height;
    private int stayTime;

    public Coordinator(double lng, double lat, int height, int stayTime) {
        this.lng = lng;
        this.lat = lat;
        this.height = height;
        this.stayTime = stayTime;
    }

    public int getHeight() {
        return height;
    }

    public int getStayTime() {
        return stayTime;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setStayTime(int stayTime) {
        this.stayTime = stayTime;
    }

    @NonNull
    public String toString() {
        return "经度：" + lng + "\n纬度：" + lat + "\n高度：" + height + "米\n悬停时间：" + stayTime + "秒";
    }
}
