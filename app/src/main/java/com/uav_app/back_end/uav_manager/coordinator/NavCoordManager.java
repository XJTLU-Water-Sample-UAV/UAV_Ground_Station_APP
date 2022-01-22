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

    public Coordinator getCoord(int coordNum) {
        if (coordNum < coordinators.size()) {
            return coordinators.get(coordNum);
        } else {
            return null;
        }
    }

    public double getLng(int coordNum) {
        if (coordNum < coordinators.size()) {
            Coordinator coordinator = coordinators.get(coordNum);
            return coordinator.getLng();
        } else {
            return 0xFF;
        }
    }

    public double getLat(int coordNum) {
        if (coordNum < coordinators.size()) {
            Coordinator coordinator = coordinators.get(coordNum);
            return coordinator.getLat();
        } else {
            return 0xFF;
        }
    }

    public double getHeight(int coordNum) {
        if (coordNum < coordinators.size()) {
            Coordinator coordinator = coordinators.get(coordNum);
            return coordinator.getHeight();
        } else {
            return 0xFF;
        }
    }

    public double getStayTime(int coordNum) {
        if (coordNum < coordinators.size()) {
            Coordinator coordinator = coordinators.get(coordNum);
            return coordinator.getStayTime();
        } else {
            return 0xFF;
        }
    }

    public void setCoord(int coordNum, double lng, double lat, int height, int stayTime) {
        if (coordNum < coordinators.size()) {
            Coordinator coordinator = coordinators.get(coordNum);
            coordinator.setLng(lng);
            coordinator.setLat(lat);
            coordinator.setHeight(height);
            coordinator.setStayTime(stayTime);
        }
    }

    public void deleteCoord(int coordNum) {
        if (coordNum < coordinators.size()) {
            coordinators.remove(coordNum);
        }
    }

    public void deleteAll() {
        coordinators.clear();
    }

    public int getCoordNum() {
        return coordinators.size();
    }

    public String getCoordDescription(int coordNum) {
        return "航点" + (coordNum + 1) + "\n" + coordinators.get(coordNum).toString();
    }
}