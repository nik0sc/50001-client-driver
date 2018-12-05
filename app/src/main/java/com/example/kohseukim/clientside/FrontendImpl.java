package com.example.kohseukim.clientside;

import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.google.android.gms.maps.model.Polyline;

import static android.location.Location.distanceBetween;

//TODO create a map reference :O

public class FrontendImpl implements FrontEnd {

    private final String Tag = "frontend";
    private ArrayList<Marker> markerlist = new ArrayList<Marker>();

    private FirebaseFirestore db;
    private Context mContext = App.getContext();
    private GoogleMap mMap = MapsActivity.mMap;

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

    public void showAmbulance(){
        db = FirebaseFirestore.getInstance();
        //TODO pull from firebase
        db.collection("ambulances").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d(Tag,document.getId()+ " => " + document.getData());
                        String str = document.getData().toString();
                        String[] point_t = str.split(",");
                        double x = Double.parseDouble(point_t[0].trim());
                        double y = Double.parseDouble(point_t[1].trim());
                        GeoPoint point = new GeoPoint(x,y);
                        MarkerOptions markeropts = new MarkerOptions();
                        markeropts.position(new LatLng(point.getLatitude(),point.getLongitude()));
                        Marker marker = mMap.addMarker(markeropts);
                        markerlist.add(marker);
                    }
                }else{
                    Log.w(Tag,"Error getting documents",task.getException());
                }
            }
        });

    }

    public void updateAmbulance(){
        db = FirebaseFirestore.getInstance();
        //TODO pull from firebase
        db.collection("ambulances").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d(Tag,document.getId()+ " => " + document.getData());
                        String str = document.getData().toString();
                        String[] point_t = str.split(",");
                        double x = Double.parseDouble(point_t[0].trim());
                        double y = Double.parseDouble(point_t[1].trim());
                        GeoPoint point = new GeoPoint(x,y);
                        MarkerOptions markeropts = new MarkerOptions();
                        markeropts.position(new LatLng(point.getLatitude(),point.getLongitude()));
                        Marker marker = mMap.addMarker(markeropts);
                        markerlist.add(marker);
                    }
                }else{
                    Log.w(Tag,"Error getting documents",task.getException());
                }
            }
        });

    }

    public void dropAmbulance(){
        for(Marker m : markerlist){
            m.remove();
        }
    }

//    public void showRadius(Coordinates car,AlertType alert){
//        //TODO called on radius
//        //need to pull map from MapsActivity or get a map reference to it
//        GoogleMap map;
//        GeoPoint center;
//        center = new GeoPoint(car.getLat(), car.getLon());
//        if(alert == AlertType.LEVEL_2 ){
//            Circle circle = map.addCircle(new CircleOptions().center(center).radius(1200).strokeColor(Color.BLUE).fillColor(Color.BLUE));
//        }
//        else if(alert == AlertType.LEVEL_1){
//            Circle circle = map.addCircle(new CircleOptions().center(center).radius(1200).strokeColor(Color.RED).fillColor(Color.RED));
//        }
//    }
//
//    public void dropRadius(){
//        //TODO called on drop ambulance
//    }

    public void showRoute(List<Coordinates> route){
        ArrayList<GeoPoint> routeline = new ArrayList<GeoPoint>();
        for(Coordinates c : route){//pre processing
            GeoPoint add = new GeoPoint(c.getLat(),c.getLon());
            routeline.add(add);
        }
        PolylineOptions lineopts = new PolylineOptions();
        for(GeoPoint p : routeline){
            LatLng l = new LatLng(p.getLatitude(),p.getLongitude());
            lineopts = lineopts.add(l).width(3).color(Color.BLACK);
        }
        Polyline line = mMap.addPolyline(lineopts);
    }

    public void updateRoute(List<Coordinates> route){
        //TODO override route, do a coordinate comparison?
        mMap.clear();
        ArrayList<GeoPoint> routeline = new ArrayList<GeoPoint>();
        for(Coordinates c : route){//pre processing
            GeoPoint add = new GeoPoint(c.getLat(),c.getLon());
            routeline.add(add);
        }
        PolylineOptions lineopts = new PolylineOptions();
        for(GeoPoint p : routeline){
            LatLng l = new LatLng(p.getLatitude(),p.getLongitude());
            lineopts = lineopts.add(l).width(3).color(Color.BLACK);
        }
        Polyline line = mMap.addPolyline(lineopts);
    }

    public void dropRoute(){
        //TODO set a route state to 0? also remove the route from being shown
        mMap.clear();
    }

}
