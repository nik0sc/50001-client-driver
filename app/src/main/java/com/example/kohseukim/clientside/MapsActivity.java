package com.example.kohseukim.clientside;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import static com.example.kohseukim.clientside.FrontEnd.AlertType.LEVEL_1;
import static com.example.kohseukim.clientside.FrontEnd.AlertType.LEVEL_2;
import static com.google.android.gms.maps.model.JointType.DEFAULT;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "Main";
    FusedLocationProviderClient mFusedLocationClient;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    PolylineOptions lineOptions = null;
    com.google.android.gms.maps.model.Polyline polylineFinal;
    List<com.google.android.gms.maps.model.Polyline> allpolylines = new ArrayList<Polyline>();
    private FirebaseFirestore db;
    List<String> activeAmbu = new ArrayList<>();

    //private FrontEnd frontEnd;
    private BackEnd backend;
    private FrontendImpl frontend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(TAG, "onCreate");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Sound alert for Level 1

        // Reference to level 1 button
        //Button map_btn_one = findViewById(R.id.map_btn_one);

        final MediaPlayer levelonealertsound = MediaPlayer.create(this, R.raw.levelonealertsound);


        // when the button is clicked, we go to the level 1 alert
//        map_btn_one.setOnClickListener(new View.OnClickListener() {
//
//
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), LevelOneActivity.class);
//                startActivity(i);
//                levelonealertsound.start();
//
//            }
//        });
//
//
//        // Reference to level 2 button
//        Button map_btn_two = findViewById(R.id.map_btn_two);

        //final MediaPlayer levelTwoAlertSound = MediaPlayer.create(this, R.raw.leveltwoalert);


        // when the button is clicked, we ego to the level 2 alert
//        map_btn_two.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MapsActivity.this, LevelTwoActivity.class);
//                startActivity(i);
//                overridePendingTransition(0, 0);
//            }
//        });

        //frontend = new FrontendImpl();

        backend = new BackendImpl(new FrontendImpl(mMap), getApplicationContext());
        backend.start("hi");

        db = FirebaseFirestore.getInstance();

        ////// NOTE: Buttons are used to trigger alert popup is temporary, since we do not have radius data yet //////

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng sg = new LatLng(1.2848, 103.8439);
        mMap.addMarker(new MarkerOptions().position(sg).title("Marker in SG"));
        float zoomLevel = 16.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sg, zoomLevel)); */

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();


        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // creates and show the map
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); // two minute interval of updating the location. number must be in milliseconds
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                mCurrLocationMarker = mMap.addMarker(markerOptions);*/


                //move map camera
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16.0f).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            }

//            db.collection("AmbulanceSide").addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                    //List<String> activeAmbu = new ArrayList<>();
//                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots){
//                        List<String> activeAmbu = new ArrayList<>();
//                        if (doc.get("Route") != null){
//                            activeAmbu.add(doc.getId());
//                        }
//                    }
//                }
//            });
//
//            for(final String a: activeAmbu){
//                db.collection("AmbulanceSide").document(a).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        Ambulance ambulance = documentSnapshot.toObject(Ambulance.class);
//
//                        float[] distance = new float[1];
//                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
//                                ambulance.getLocation().getLatitude()/1E6, ambulance.getLocation().getLatitude()/1E6,distance);
//
//
//
//                        if(distance[0] < 1200){
//                            LatLng origin = new LatLng(ambulance.getLocation().getLatitude()/1E6, ambulance.getLocation().getLongitude()/1E6);
//                            try{
//                                LatLng dest = new LatLng(ambulance.getDestination().getLatitude()/1E6, ambulance.getDestination().getLongitude()/1E6);
//                                String url = getUrl(origin, dest);
//                                Log.d("onMapClick", url.toString());
//                                FetchUrl FetchUrl = new FetchUrl();
//
//                                // Start downloading json data from Google Directions API
//                                FetchUrl.execute(url);
//
//                                if(distance[0] <800)
//                                frontEnd.showAlert(LEVEL_1);
//
//                                else
//                                    frontEnd.showAlert(LEVEL_2);
//
//
////                                        allpolylines.get(1).remove();
//
//
//                            }catch (IndexOutOfBoundsException e){
//
//                            }
//
//
//                        }
//
//
//
//
//                    }
//                });
//            }



    }};

//    private class Ambulance{
//        private String id;
//        private String email;
//        private GeoPoint Location;
//        private GeoPoint Destination;
//        private Array route;
//
//        private Ambulance() {}
//
//        private Ambulance(String id, String email, GeoPoint Location, GeoPoint Destination ,Array route){
//            this.id = id;
//            this.email = email;
//            this.Location = Location;
//            this.route = route;
//            this.Destination = Destination;
//
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//
//        public GeoPoint getLocation() {
//            return Location;
//        }
//
//        public void setLocation(GeoPoint location) {
//            Location = location;
//        }
//
//        public Array getRoute() {
//            return route;
//        }
//
//        public void setRoute(Array route) {
//            this.route = route;
//        }
//
//        public GeoPoint getDestination() {
//            return Destination;
//        }
//
//        public void setDestination(GeoPoint destination) {
//            this.Destination = destination;
//        }
//    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;  // i actually don't know what is this for
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request. in this case, none
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();

        backend.stop();
    }





    /**
     * A class to parse the Google Places in JSON format
     */
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//
//        // Parsing the data in non-ui thread
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//
//            JSONObject jObject;
//            List<List<HashMap<String, String>>> routes = null;
//
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                Log.d("ParserTask",jsonData[0].toString());
//                DataParser parser = new DataParser();
//                Log.d("ParserTask", parser.toString());
//
//                // Starts parsing data
//                routes = parser.parse(jObject);
//                Log.d("ParserTask","Executing routes");
//                Log.d("ParserTask",routes.toString());
//
//            } catch (Exception e) {
//                Log.d("ParserTask",e.toString());
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        // Executes in UI thread, after the parsing process
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            ArrayList<LatLng> points;
//
//
//
//            // Traversing through all the routes
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList<>();
//                lineOptions = new PolylineOptions();
//
//                // Fetching i-th route
//                List<HashMap<String, String>> path = result.get(i);
//
//                // Fetching all the points in i-th route
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//                //Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(20);
//                lineOptions.color(Color.BLUE);
//                lineOptions.jointType(DEFAULT);
//
//                Log.d("onPostExecute","onPostExecute lineoptions decoded");
//
//            }
//
//            // Drawing polyline in the Google Map for the i-th route
//            if(lineOptions != null) {
//                polylineFinal = mMap.addPolyline(lineOptions);
//                if(allpolylines.size() > 1 ){
//                    for ( Polyline l: allpolylines){
//                        l.remove();
//                    }
//                }
//                allpolylines.add(polylineFinal);
//                Log.i("POLY", "Size: " + allpolylines.size());
//            }
//            else {
//                Log.d("onPostExecute","without Polylines drawn");
//            }
//        }
//    }
//
//    private String getUrl(LatLng origin, LatLng dest) {
//
//        // Origin of route
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//
//        // Destination of route
//        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//
//
//        // Sensor enabled
//        String sensor = "sensor=false";
//
//        String key = "key=AIzaSyDmN5MP07wzzwDBrBXeDbSlmWuNpB_l1lw";
//
//
//        // Building the parameters to the web service
//        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + key;
//
//
//        // Output format
//        String output = "json";
//
//        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//
//
//        return url;
//    }
//
//    /**
//     * A method to download json data from url
//     */
//    private String downloadUrl(String strUrl) throws IOException {
//        String data = "";
//        InputStream iStream = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(strUrl);
//
//            // Creating an http connection to communicate with url
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            // Connecting to url
//            urlConnection.connect();
//
//            // Reading data from url
//            iStream = urlConnection.getInputStream();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
//
//            StringBuffer sb = new StringBuffer();
//
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//
//            data = sb.toString();
//            Log.d("downloadUrl", data.toString());
//            br.close();
//
//        } catch (Exception e) {
//            Log.d("Exception", e.toString());
//        } finally {
//            iStream.close();
//            urlConnection.disconnect();
//        }
//        return data;
//    }
//
//    public GoogleMap getMap() {
//
//        return mMap;
//    }
//
//    // Fetches data from url passed
//    private class FetchUrl extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            // For storing data from web service
//            String data = "";
//
//            try {
//                // Fetching the data from web service
//                data = downloadUrl(url[0]);
//                Log.d("Background Task data", data.toString());
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            ParserTask parserTask = new ParserTask();
//
//            // Invokes the thread for parsing the JSON data
//            parserTask.execute(result);
//
//        }
//    }

}

