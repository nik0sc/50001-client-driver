package com.example.kohseukim.clientside;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public interface FrontEnd {
    enum AlertType {
        LEVEL_1, LEVEL_2
    }

    void showAlert(AlertType type);
    void dropAlert();

    void showAmbulance(GeoPoint location, double heading);
    void updateAmbulance(GeoPoint location, double heading);
    void dropAmbulance();

    void showRadius(double radius);
    void dropRadius();

    void showRoute(List<GeoPoint> route);
    void updateRoute(List<GeoPoint> route);
    void dropRoute();
}

