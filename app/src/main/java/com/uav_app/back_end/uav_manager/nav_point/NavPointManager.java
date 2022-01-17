package com.uav_app.back_end.uav_manager.nav_point;

import java.util.ArrayList;
import java.util.List;

public class NavPointManager {
    private final List<NavPoint> points;

    public NavPointManager() {
        points = new ArrayList<>();
    }

    public void addPoint(double lng, double lat, int height, int stayTime) {
        NavPoint point = new NavPoint(lng, lat, height, stayTime);
        points.add(point);
    }

    public NavPoint getPoint(int pointNum) {
        if (pointNum < points.size()) {
            return points.get(pointNum);
        } else {
            return null;
        }
    }

    public double getLng(int pointNum) {
        if (pointNum < points.size()) {
            NavPoint point = points.get(pointNum);
            return point.getLng();
        } else {
            return 0xFF;
        }
    }

    public double getLat(int pointNum) {
        if (pointNum < points.size()) {
            NavPoint point = points.get(pointNum);
            return point.getLat();
        } else {
            return 0xFF;
        }
    }

    public double getHeight(int pointNum) {
        if (pointNum < points.size()) {
            NavPoint point = points.get(pointNum);
            return point.getHeight();
        } else {
            return 0xFF;
        }
    }

    public double getStayTime(int pointNum) {
        if (pointNum < points.size()) {
            NavPoint point = points.get(pointNum);
            return point.getStayTime();
        } else {
            return 0xFF;
        }
    }

    public void setPoint(int pointNum, double lng, double lat, int height, int stayTime) {
        if (pointNum < points.size()) {
            NavPoint point = points.get(pointNum);
            point.setLng(lng);
            point.setLat(lat);
            point.setHeight(height);
            point.setStayTime(stayTime);
        }
    }

    public void deletePoint(int pointNum) {
        if (pointNum < points.size()) {
            points.remove(pointNum);
        }
    }

    public void deleteAll() {
        points.clear();
    }

    public int getPointNum() {
        return points.size();
    }

    public String getPointDescription(int pointNum) {
        return "航点" + (pointNum + 1) + "\n" + points.get(pointNum).toString();
    }
}