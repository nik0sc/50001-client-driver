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

public class FrontendImpl implements FrontEnd {

    private AlertType state = null;
    public static final String TAG = "FrontendImpl";
    private ArrayList<Marker> markerlist = new ArrayList<Marker>();
    private ArrayList<Polyline> polylinelist = new ArrayList<Polyline>();

    private Context mContext = MyApplication.getAppContext();
    private GoogleMap mMap;

    public FrontendImpl(GoogleMap map){

        mMap = map;

    }

    public void showAlert(AlertType alert){

        Intent intent;

        switch (alert) {
            case LEVEL_1:
                if(state != AlertType.LEVEL_1){
                    Log.d(TAG, "showAlert: Level 1 alert");
                    intent = new Intent(mContext, LevelOneActivity.class);
                    state = AlertType.LEVEL_1;
                    break;
                }
            case LEVEL_2:
                if(state != AlertType.LEVEL_2){
                    Log.d(TAG, "showAlert: Level 2 alert");
                    intent = new Intent(mContext, LevelTwoActivity.class);
                    state = AlertType.LEVEL_2;
                    break;
                }
            default:
                Log.e(TAG, "showAlert: Unsupported alert type");
                return;
        }

        mContext.startActivity(intent);
    }

    public void dropAlert(){
        //TODO nulls alert
        Intent intent = new Intent(mContext, MapsActivity.class);
        mContext.startActivity(intent);
    }

    public void showAmbulance(GeoPoint location, double heading){
        LatLng l = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions mo = new MarkerOptions().position(l).title("Ambulance");
        Marker m = mMap.addMarker(mo);
        markerlist.add(m);
    }


    public void updateAmbulance(GeoPoint location, double heading){
        for(Marker m : markerlist){
            m.remove();
        }
        LatLng l = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions mo = new MarkerOptions().position(l).title("Ambulance");
        Marker m = mMap.addMarker(mo);
        markerlist.add(m);
    }


    public void dropAmbulance(){
        for(Marker m : markerlist){
            m.remove();
        }
    }

    public void showRadius(double radius){
        //does nothing
    }

    public void dropRadius(){
        //does nothing
    }

    public void showRoute(List<GeoPoint> route){
        PolylineOptions po = new PolylineOptions();
        for(GeoPoint g :route){
            LatLng l = new LatLng(g.getLatitude(),g.getLongitude());
            po.add(l);
        }
        Polyline p = mMap.addPolyline(po);
        polylinelist.add(p);
    }

    public void updateRoute(List<GeoPoint> route){
        for(Polyline p : polylinelist){
            p.remove();
        }
        PolylineOptions po = new PolylineOptions();
        for(GeoPoint g :route){
            LatLng l = new LatLng(g.getLatitude(),g.getLongitude());
            po.add(l);
        }
        Polyline p = mMap.addPolyline(po);
        polylinelist.add(p);
    }

    public void dropRoute(){
        for(Polyline p : polylinelist){
            p.remove();
        }
    }

}
