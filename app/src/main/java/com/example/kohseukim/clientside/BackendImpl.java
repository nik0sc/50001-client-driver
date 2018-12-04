package com.example.kohseukim.clientside;

import android.content.Context;
import android.util.Log;

import static android.location.Location.distanceBetween;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import com.google.android.gms.location.LocationServices;

import javax.annotation.Nullable;

public class BackendImpl implements BackEnd {
    public static final String TAG = "BackendImpl";

    public static final int level1DistanceCutoff = 800;
    public static final int level2DistanceCutoff = 1200;

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
    // Current driver
    private Driver driver;
    // Reference to driver in db
    private DocumentReference fbDriver = null;

    // Pull these constants from R.string later
    private final String fbVehicles;
    private final String fbAmbulances;
    private final String fbAmbulanceIsActive;

    /*
     * Create a new backend class
     * Do not pass in the activity context!!
     */
    public BackendImpl(FrontEnd frontEnd, Context appContext) {
        this.frontEnd = frontEnd;
        this.appContext = appContext;
        db = FirebaseFirestore.getInstance();
        driver = new Driver();

        fbVehicles = appContext.getString(R.string.fb_vehicles);
        fbAmbulances = appContext.getString(R.string.fb_ambulances);
        fbAmbulanceIsActive = appContext.getString(R.string.fb_ambulance_isActive);

        if (level1DistanceCutoff > level2DistanceCutoff) {
            throw new AssertionError("Level 1 is more than level 2 distance");
        }
    }

    /*
     * Publish the client and begin monitoring the /ambulances document
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




    }

    // On ambulance update, iterate through all the updated ones and find out which one is the closest
    private void onAmbulanceUpdate(QueryDocumentSnapshot doc) {

    }

    // On ambulance update, iterate through
    private void onAmbulanceRemove(QueryDocumentSnapshot doc) {

    }

    //

    private FrontEnd.AlertType calculateAlertType(Coordinates car, Coordinates amb) {
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
        distanceBetween(car.getLat(), car.getLon(), amb.getLat(), amb.getLon(), distance);

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
    }

    @Override
    public void stop() {
        if (fbDriver != null) {
            fbDriver.delete();
        }
        if (registration != null) {
            registration.remove();
        }
    }
}
