package com.example.kohseukim.clientside;

import com.google.firebase.firestore.GeoPoint;
import static android.location.Location.distanceBetween;

import java.util.Comparator;
import java.util.Map;

/**
 * A Comparator that can compare ambulance cache entries by their distance from the current point.
 * You must create a new Comparator every time the location updates.
 * This is by design
 * Comparator doesn't check for null pointers: NPE is your problem
 */
public class AmbulanceDistanceComparator implements Comparator<Map.Entry<String,Ambulance>> {
    // No public setters - otherwise the correct ordering could change while the sort or queue
    // uses the old ordering. Simpler to just create a new comparator every time
    private GeoPoint currentLocation;

    /**
     * Create a new AmbulanceDistanceComparator.
     * @param currentLocation the current location to use when comparing distances
     */
    public AmbulanceDistanceComparator(GeoPoint currentLocation){
        this.currentLocation = currentLocation;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public int compare(Map.Entry<String, Ambulance> o1, Map.Entry<String, Ambulance> o2) {
        float[] o1DistArray = {0};
        float[] o2DistArray = {0};

        GeoPoint o1geo = o1.getValue().getCurrentLocation();
        GeoPoint o2geo = o2.getValue().getCurrentLocation();

        // Obtain distance to ambulance 1 and 2
        distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                o1geo.getLatitude(), o1geo.getLongitude(), o1DistArray);

        distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                o2geo.getLatitude(), o2geo.getLongitude(), o2DistArray);

        // Compare distances directly
        return Float.compare(o1DistArray[0], o2DistArray[0]);
    }
}
