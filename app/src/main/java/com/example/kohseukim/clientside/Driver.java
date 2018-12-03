package com.example.kohseukim.clientside;

import com.google.firebase.firestore.*;

public class Driver {
    private String alertLevel;
    private String alertResponded;
    private DocumentReference inRadiusOf;
    private GeoPoint location;

    public Driver() {
        this.alertLevel = null;
        this.alertResponded = null;
        this.inRadiusOf = null;
        this.location = null;
    }

    public Driver(String alertLevel, String alertResponded, DocumentReference inRadiusOf, GeoPoint location) {
        this.alertLevel = alertLevel;
        this.alertResponded = alertResponded;
        this.inRadiusOf = inRadiusOf;
        this.location = location;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getAlertResponded() {
        return alertResponded;
    }

    public void setAlertResponded(String alertResponded) {
        this.alertResponded = alertResponded;
    }

    public DocumentReference getInRadiusOf() {
        return inRadiusOf;
    }

    public void setInRadiusOf(DocumentReference inRadiusOf) {
        this.inRadiusOf = inRadiusOf;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
