package com.example.kohseukim.clientside;

import com.google.firebase.firestore.*;
import java.util.List;

public class Ambulance {
    private GeoPoint currentLocation;
    private List<GeoPoint> route;
    private boolean isActive;

    public Ambulance() {
        currentLocation = null;
        route = null;
        isActive = false;
    }

    public Ambulance(GeoPoint currentLocation, List<GeoPoint> route, boolean isActive) {
        this.currentLocation = currentLocation;
        this.route = route;
        this.isActive = isActive;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isActive() {
        return isActive;
    }

    // This method is here for firestore's resolution of fields and accessors
    public boolean getIsActive() {
        return isActive();
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<GeoPoint> getRoute() {
        return route;
    }

    public void setRoute(List<GeoPoint> route) {
        this.route = route;
    }
}
