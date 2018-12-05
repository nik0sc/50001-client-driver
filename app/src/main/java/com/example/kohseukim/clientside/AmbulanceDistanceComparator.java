package com.example.kohseukim.clientside;

import com.google.firebase.firestore.GeoPoint;
import static android.location.Location.distanceBetween;

import java.util.Comparator;

/**
 * A Comparator that can compare ambulances by their distance from the current point.
 * You must create a new Comparator every time the location updates.
 * This is by design
 */
public class AmbulanceDistanceComparator implements Comparator<Ambulance> {
    // No public accessors - otherwise the correct ordering could change while the sort or queue
    // uses the old ordering. Simpler to just create a new comparator every time
    private GeoPoint currentLocation;

    /**
     * Create a new AmbulanceDistanceComparator.
     * @param currentLocation the current location to use when comparing distances
     */
    public AmbulanceDistanceComparator(GeoPoint currentLocation){
        this.currentLocation = currentLocation;
    }

    @Override
    public int compare(Ambulance o1, Ambulance o2) {
        float[] o1DistArray = {0};
        float[] o2DistArray = {0};

        // Obtain distance to ambulance 1 and 2
        distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                o1.getCurrentLocation().getLatitude(), o1.getCurrentLocation().getLongitude(),
                o1DistArray);

        distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                o2.getCurrentLocation().getLatitude(), o2.getCurrentLocation().getLongitude(),
                o2DistArray);

        // Compare distances directly
        return Float.compare(o1DistArray[0], o2DistArray[0]);
    }
}
