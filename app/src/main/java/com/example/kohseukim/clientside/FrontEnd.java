package com.example.kohseukim.clientside;

import java.util.List;

class Coordinates {
    private double lat;
    private double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Coordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}

public interface FrontEnd {
    enum AlertType {
        LEVEL_1, LEVEL_2
    }

    public void showAlert(AlertType type);
    public void dropAlert(AlertType type);

    public void showAmbulance(double lat, double lon, double heading);
    public void updateAmbulance(double lat, double lon, double heading);
    public void dropAmbulance();

    public void showRadius(double radius);
    public void dropRadius();

    public void showRoute(List<Coordinates> route);
    public void updateRoute(List<Coordinates> route);
    public void dropRoute();
}

