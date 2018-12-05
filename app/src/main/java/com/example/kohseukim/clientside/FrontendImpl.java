package com.example.kohseukim.clientside;

import android.content.Intent;
import android.content.Context;

import java.util.List;

import com.google.firebase.firestore.*;

public class FrontendImpl implements FrontEnd {

    private FirebaseFirestore db;
    private Context mContext = getApplicationContext();

    public void showAlert(AlertType alert){
        db = FirebaseFirestore.getInstance();
        if(alert == null) {
            Intent intent = new Intent(mContext, LevelOneActivity.class);
            mContext.startActivity(intent);
        }
        else if(alert == AlertType.LEVEL_1){
            Intent intent = new Intent(mContext, LevelTwoActivity.class);
            mContext.startActivity(intent);
        }
    }

    public void dropAlert(AlertType alert){
        //TODO nulls alert
        db = FirebaseFirestore.getInstance();
        if(alert == AlertType.LEVEL_2){
            Intent intent = new Intent(mContext,LevelOneActivity.class);
            mContext.startActivity(intent);
            }
        else if(alert == AlertType.LEVEL_1){
            Intent intent = new Intent(mContext, MapsActivity.class);
            mContext.startActivity(intent);
        }
        }

    public void showAmbulance(double lat, double lon){
        db = FirebaseFirestore.getInstance();
        //TODO pull from firebase
    }

    public void updateAmbulance(double lat, double lon){
        db = FirebaseFirestore.getInstance();


    }
    public void dropAmbulance(){
        //TODO update firebase
        db = FirebaseFirestore.getInstance();

    }

    public void showRadius(double radius){
        //TODO called on radius
        db = FirebaseFirestore.getInstance();
        if(1 == 1){}
    }

    public void dropRadius(){
        //TODO called on drop ambulance
    }

    public void showRoute(List<Coordinates> route){
        for(Coordinates c : route){
            //put point on map
        }
    }

    public void updateRoute(List<Coordinates> route){
        //TODO override route, do a coordinate comparison?
    }

    public void dropRoute(){
        //TODO set a route state to 0? also remove the route from being shown
    }

}
