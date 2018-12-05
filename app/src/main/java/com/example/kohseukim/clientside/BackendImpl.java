package com.example.kohseukim.clientside;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import static android.location.Location.distanceBetween;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.annotation.Nullable;

public class BackendImpl implements BackEnd {
    public static final String TAG = "BackendImpl";

    // Distance cutoff in metres
    public static final int level1DistanceCutoff = 800;
    public static final int level2DistanceCutoff = 1200;

    // Minimum number of seconds to wait before querying again (Rate limiting)
    public static final int minInterval = 5;

    // Seconds between location updates
    public static final int locationInterval = 5;

    // Frontend to display icons and plot routes etc
    private FrontEnd frontEnd;

    // Don't use activity context without wrapping in a weak reference
    private Context appContext;

    // Cloud Firestore instance
    private FirebaseFirestore db;

    // Cloud Firestore listener (unregister when stopping)
    private ListenerRegistration registration = null;

    // Ambulance being alerted on
    private Ambulance activeAmbulance = null;

    // Reference to ambulance in db
    private DocumentReference fbActiveAmbulance = null;

    // Ambulance cache
    private final HashMap<String, Ambulance> ambulanceCache;

    // Current driver
    private Driver driver;

    // Reference to driver in db
    private DocumentReference fbDriver = null;

    // Ambulance sorting class
    // declared final to prevent changing the reference and defeating the synchronization
    private final AmbulanceSorter ambulanceSorter;

    // Fused location provider
    private FusedLocationProviderClient locationClient;

    // Location callback to receive location updates
    private LocationCallback locationCallback = null;

    // Most recent location
    private Location mostRecentLocation = null;

    // Pull these constants from R.string later
    private final String fbVehicles;
    private final String fbAmbulances;
    private final String fbAmbulanceIsActive;

    /**
     * Create a new backend object
     * @param frontEnd The frontend object
     * @param appContext The application context (Do not pass in the activity context!!)
     */
    public BackendImpl(FrontEnd frontEnd, Context appContext) {
        this.frontEnd = frontEnd;
        this.appContext = appContext;

        db = FirebaseFirestore.getInstance();
        driver = new Driver();
        ambulanceCache = new HashMap<>();
        locationClient = LocationServices.getFusedLocationProviderClient(appContext);
        ambulanceSorter = new AmbulanceSorter();

        fbVehicles = appContext.getString(R.string.fb_vehicles);
        fbAmbulances = appContext.getString(R.string.fb_ambulances);
        fbAmbulanceIsActive = appContext.getString(R.string.fb_ambulance_isActive);

        // Wrong place for this assertion
//        if (level1DistanceCutoff > level2DistanceCutoff) {
//            throw new AssertionError("Level 1 is more than level 2 distance");
//        }
    }

    /**
     * Publish the client and begin monitoring the /ambulances collection
     * @param id Id for the client
     */
    @Override
    public void start(String id) {
        // Publish the driver client and save the reference for later
        db.collection(fbVehicles).add(driver).addOnSuccessListener(
            new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    fbDriver = documentReference;
                }
            }
        );
        Log.d(TAG, "start: Subscribed");

        // Ambulance listener
        Query q = db.collection(fbAmbulances).whereEqualTo(fbAmbulanceIsActive,true);
        registration = q.addSnapshotListener(new EventListener<QuerySnapshot>() {
            // This runs when the fb_ambulances document changes
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // TODO Signal frontend to restart the backend
                    Log.e(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "New ambulance: " + dc.getDocument().getData());
                            onAmbulanceUpdate(dc.getDocument());
                            break;
                        case MODIFIED:
                            Log.d(TAG, "Modified ambulance: " + dc.getDocument().getData());
                            onAmbulanceUpdate(dc.getDocument());
                            break;
                        case REMOVED:
                            Log.d(TAG, "Removed ambulance: " + dc.getDocument().getData());
                            onAmbulanceRemove(dc.getDocument());
                            break;
                    }
                }
            }
        });

        startLocationUpdates();

        Log.i(TAG, "start: Started backend");
    }

    private void startLocationUpdates() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mostRecentLocation = locationResult.getLastLocation();
                // TODO signal recalculation
            }
        };

        try {
            // Begin location update
            locationClient.requestLocationUpdates(
                    LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(locationInterval),
                    locationCallback,
                    null
            );
        } catch (SecurityException e) {
            Toast.makeText(appContext, "Not allowed to get location", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startLocationUpdates: SecurityException!", e);
        }

        Log.d(TAG, "startLocationUpdates: Registered location callback");
    }

    private void stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "stopLocationUpdates: Unregistered location callback");
    }

    private GeoPoint mostRecentLocationAsGeoPoint() {
        if (mostRecentLocation == null) {
            return null;
        }

        return new GeoPoint(mostRecentLocation.getLatitude(), mostRecentLocation.getLongitude());
    }

    // Update cache and signal recalculation
    private void onAmbulanceUpdate(QueryDocumentSnapshot doc) {
        String id = doc.getId();
        Ambulance updated = doc.toObject(Ambulance.class);

        ambulanceCache.put(id, updated);

        ambulanceSorter.updateAmbulances(ambulanceCache.values());
        ambulanceSorter.updateLocation(mostRecentLocationAsGeoPoint());

        // TODO recalculate
    }

    // Delete and signal recalculation
    private void onAmbulanceRemove(QueryDocumentSnapshot doc) {
        String id = doc.getId();
        ambulanceCache.remove(id);

        // TODO recalculate
    }

    private void recalculate(){
        // Obtain the latest sorted ambulance

    }

    public static FrontEnd.AlertType calculateAlertType(GeoPoint car, GeoPoint amb) {
        float[] distance = {0};
        /*
        https://developer.android.com/reference/android/location/Location.html

        Computes the approximate distance in meters between two locations, and optionally the
        initial and final bearings of the shortest path between them. Distance and bearing are
        defined using the WGS84 ellipsoid.

        The computed distance is stored in results[0]. If results has length 2 or greater, the
        initial bearing is stored in results[1]. If results has length 3 or greater, the final
        bearing is stored in results[2].
         */
        distanceBetween(car.getLatitude(), car.getLongitude(),
                amb.getLatitude(), amb.getLongitude(), distance);

        if (distance[0] < level1DistanceCutoff) {
            return FrontEnd.AlertType.LEVEL_1;
        } else if (distance[0] < level2DistanceCutoff) {
            return FrontEnd.AlertType.LEVEL_2;
        } else {
            return null;
        }
    }

    @Override
    public void acknowledgeAlert() {
        if (fbDriver != null) {
            driver.setAlertResponded(true);
            fbDriver.set(driver);
        }
        Log.d(TAG, "acknowledgeAlert: Acknowledged alert");
    }

    @Override
    public void stop() {
        stopLocationUpdates();

        if (fbDriver != null) {
            fbDriver.delete();
        }
        if (registration != null) {
            registration.remove();
        }

        Log.i(TAG, "stop: Stopped backend");
    }
}
