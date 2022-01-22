package com.uav_app.back_end.uav_manager.coordinator;

import java.util.ArrayList;
import java.util.List;

public class NavCoordManager {
    private final List<Coordinator> coordinators;

    public NavCoordManager() {
        coordinators = new ArrayList<>();
    }

    public void addPoint(double lng, double lat, int height, int stayTime) {
        Coordinator coordinator = new Coordinator(lng, lat, height, stayTime);
        coordinators.add(coordinator);
    }

    public Coordinator getPoint(int pointNum) {
        if (pointNum < coordinators.size()) {
            return coordinators.get(pointNum);
        } else {
            return null;
        }
    }

    public double getLng(int pointNum) {
        if (pointNum < coordinators.size()) {
            Coordinator point = coordinators.get(pointNum);
            return point.getLng();
        } else {
            return 0xFF;
        }
    }

    public double getLat(int pointNum) {
        if (pointNum < coordinators.size()) {
            Coordinator point = coordinators.get(pointNum);
            return point.getLat();
        } else {
            return 0xFF;
        }
    }

    public double getHeight(int pointNum) {
        if (pointNum < coordinators.size()) {
            Coordinator point = coordinators.get(pointNum);
            return point.getHeight();
        } else {
            return 0xFF;
        }
    }

    public double getStayTime(int pointNum) {
        if (pointNum < coordinators.size()) {
            Coordinator point = coordinators.get(pointNum);
            return point.getStayTime();
        } else {
            return 0xFF;
        }
    }

    public void setPoint(int pointNum, double lng, double lat, int height, int stayTime) {
        if (pointNum < coordinators.size()) {
            Coordinator point = coordinators.get(pointNum);
            point.setLng(lng);
            point.setLat(lat);
            point.setHeight(height);
            point.setStayTime(stayTime);
        }
    }

    public void deletePoint(int pointNum) {
        if (pointNum < coordinators.size()) {
            coordinators.remove(pointNum);
        }
    }

    public void deleteAll() {
        coordinators.clear();
    }

    public int getPointNum() {
        return coordinators.size();
    }

    public String getPointDescription(int pointNum) {
        return "航点" + (pointNum + 1) + "\n" + coordinators.get(pointNum).toString();
    }
}