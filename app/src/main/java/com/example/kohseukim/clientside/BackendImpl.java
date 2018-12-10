package com.example.kohseukim.clientside;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static android.location.Location.distanceBetween;

public class BackendImpl implements BackEnd {
    private static final String TAG = "BackendImpl";

    // Distance cutoff in metres
    private static final int level1DistanceCutoff = 1200;
    private static final int level2DistanceCutoff = 800;

    // Minimum number of seconds to wait before recalculating
    private static final int minInterval = 2;

    // Seconds between location updates
    private static final int locationInterval = 2;

    // Frontend to display icons and plot routes etc
    private FrontEnd frontEnd;

    // Don't use activity context without wrapping in a weak reference
    private Context appContext;

    // Cloud Firestore instance
    private FirebaseFirestore db;

    // Cloud Firestore listener (unregister when stopping)
    private ListenerRegistration registration = null;

    // Ambulance being alerted on (contains firestore id and ambulance object)
    private Map.Entry<String, Ambulance> activeAmbulance = null;

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

    // Last recalculation
    private long lastRecalculation = 0;

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
                    Log.d(TAG, "start: Driver published, id: " + fbDriver.getId());
                }
            }
        );

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

    /**
     * Update mostRecentLocation field with the best location when its available
     */
    private void startLocationUpdates() {
        // Attach callback to fusedlocationproviderclient
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mostRecentLocation = locationResult.getLastLocation();
                Log.d(TAG, "onLocationResult: Got location and updated mRL field");

                driver.setLocation(mostRecentLocationAsGeoPoint());
                if (fbDriver != null) {
                    fbDriver.set(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Successfully updated driver location");
                        }
                    });
                } else {
                    Log.w(TAG, "onLocationResult: Can't update driver location on firestore because fbDriver is null");
                }

                // Tell sorter that we have a better location
                ambulanceSorter.updateLocation(mostRecentLocationAsGeoPoint());

                // TODO signal recalculation
                recalculateAndDisplay();
            }
        };

        try {
            // Begin location update
            locationClient.requestLocationUpdates(
                    LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(locationInterval * 1000),
                    locationCallback,
                    null
            );
        } catch (SecurityException e) {
            Toast.makeText(appContext, "Not allowed to get location", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startLocationUpdates: SecurityException!", e);
        }

        Log.d(TAG, "startLocationUpdates: Registered location callback");
    }

    /**
     * Unregister location callback from startLocationUpdates()
     */
    private void stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "stopLocationUpdates: Unregistered location callback");
    }

    /**
     * Convert most recent location to Firebase GeoPoint
     * @return GeoPoint
     */
    private GeoPoint mostRecentLocationAsGeoPoint() {
        if (mostRecentLocation == null) {
            Log.e(TAG, "mostRecentLocationAsGeoPoint: Location is not updated yet");
            return null;
        }

        return new GeoPoint(mostRecentLocation.getLatitude(), mostRecentLocation.getLongitude());
    }

    /**
     * Given the location of a car and ambulance, calculate the alert level that would result
     * @param car Coordinates of car
     * @param amb Coordinates of ambulance
     * @return the alert type or null
     */
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

        if (distance[0] < level2DistanceCutoff) {
            return FrontEnd.AlertType.LEVEL_2;
        } else if (distance[0] < level1DistanceCutoff) {
            return FrontEnd.AlertType.LEVEL_1;
        } else {
            return null;
        }
    }

    /**
     * Update the ambulance cache and ambulance sorter object, then request a recalculation
     * @param doc QueryDocumentSnapshot from Firebase
     */
    private void onAmbulanceUpdate(QueryDocumentSnapshot doc) {
        String id = doc.getId();
        Ambulance updated = doc.toObject(Ambulance.class);

        ambulanceCache.put(id, updated);

        // Update ambulanceSorter and cause it to invalidate the sorted ambulance list
        ambulanceSorter.updateAmbulances(ambulanceCache);
        ambulanceSorter.updateLocation(mostRecentLocationAsGeoPoint());

        // Safe to call ambulanceSorter.getAmbulancesSorted() now

        // TODO recalculate
        recalculateAndDisplay();
    }

    /**
     * Remove an ambulance from the cache and request recalculation
      * @param doc QueryDocumentSnapshot from Firebase
     */
    private void onAmbulanceRemove(QueryDocumentSnapshot doc) {
        String id = doc.getId();
        ambulanceCache.remove(id);

        ambulanceSorter.updateAmbulances(ambulanceCache);
        ambulanceSorter.updateLocation(mostRecentLocationAsGeoPoint());

        // TODO recalculate
        recalculateAndDisplay();
    }

    private void recalculateAndDisplay(){
        Log.d(TAG, "recalculateAndDisplay: Enter recalculation");
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastRecalculation < minInterval * 1000) {
            Log.d(TAG, "recalculateAndDisplay: Interval hasn't elapsed yet");
            return;
        }

        Map.Entry<String, Ambulance> nearestAmbulanceEntry = ambulanceSorter.getNearestAmbulance();

        if (nearestAmbulanceEntry == null) {
            Log.d(TAG, "recalculateAndDisplay: No ambulance nearby");
            lastRecalculation = System.currentTimeMillis();
            return;
        }

        Ambulance nearestAmbulance = nearestAmbulanceEntry.getValue();
        String nearestAmbulanceId = nearestAmbulanceEntry.getKey();

        // Is this the nearest ambulance from last time?
        boolean sameAmbulance = (activeAmbulance != null
                && nearestAmbulanceId.equals(activeAmbulance.getKey()));

        if (!sameAmbulance) {
            // Update active ambulance
            activeAmbulance = nearestAmbulanceEntry;
            Log.d(TAG, "recalculateAndDisplay: activeAmbulance changed");
        } else {
            Log.d(TAG, "recalculateAndDisplay: no change to ambulance");
        }

        // Possible NPE at mostRecentLocation
        GeoPoint mrl = mostRecentLocationAsGeoPoint();

        if (mrl == null){
            Log.d(TAG, "recalculateAndDisplay: most recent location is not ready yet, " +
                    "if this persists there is probably a location bug");
            return;
        }

        FrontEnd.AlertType alertType = calculateAlertType(mrl, nearestAmbulance.getCurrentLocation());
        Log.d(TAG, "recalculateAndDisplay: alertType: " + alertType);

        if (alertType == null) {
            // Do nothing
            return;
        } else if (sameAmbulance) {
            frontEnd.showAlert(alertType);
            frontEnd.updateAmbulance(nearestAmbulance.getCurrentLocation(), 0);
            frontEnd.updateRoute(nearestAmbulance.getRoute());
        } else {
            frontEnd.dropAmbulance();
            frontEnd.dropRoute();

            frontEnd.showAlert(alertType);
            frontEnd.showAmbulance(nearestAmbulance.getCurrentLocation(),0);
            frontEnd.showRoute(nearestAmbulance.getRoute());
        }

        // Update the last recalculation
        long completeTime = System.currentTimeMillis();
        lastRecalculation = completeTime;
        Log.d(TAG, "recalculateAndDisplay: millis taken: " + (completeTime - currentTime));
    }


    @Override
    public void acknowledgeAlert() {
        if (fbDriver != null) {
            driver.setAlertResponded(true);
            fbDriver.set(driver);
            Log.d(TAG, "acknowledgeAlert: Acknowledged alert");

        }
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
