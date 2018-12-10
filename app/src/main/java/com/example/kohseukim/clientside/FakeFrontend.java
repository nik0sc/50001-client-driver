package com.example.kohseukim.clientside;

import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class FakeFrontend implements FrontEnd {
    public static final String TAG = "FakeFrontEnd";
    private AlertType currentAlert = null;
    
    public FakeFrontend(){
        
    }
    
    @Override
    public void showAlert(AlertType type) {
        Log.i(TAG, "showAlert: type: " + type);
        if (type == currentAlert) {
            Log.i(TAG, "showAlert: Not replacing");
        } else {
            currentAlert = type;
            Log.i(TAG, "showAlert: Replaced current");
        }
    }

    @Override
    public void dropAlert() {
        currentAlert = null;
    }

    @Override
    public void showAmbulance(GeoPoint location, double heading) {
        Log.i(TAG, "showAmbulance: location: " + location.toString() + ", heading: " + heading);
    }

    @Override
    public void updateAmbulance(GeoPoint location, double heading) {
        Log.i(TAG, "updateAmbulance: location: " + location.toString() + ", heading: " + heading);
    }

    @Override
    public void dropAmbulance() {
        Log.i(TAG, "dropAmbulance");
    }

    @Override
    public void showRadius(double radius) {
        Log.i(TAG, "showRadius: " + radius);
    }

    @Override
    public void dropRadius() {
        Log.i(TAG, "dropRadius");
    }

    @Override
    public void showRoute(List<GeoPoint> route) {
        Log.i(TAG, "showRoute: " + route);
    }

    @Override
    public void updateRoute(List<GeoPoint> route) {
        Log.i(TAG, "updateRoute: " + route);
    }

    @Override
    public void dropRoute() {
        Log.i(TAG, "dropRoute");
    }
}
