package com.example.kohseukim.clientside;

import com.google.firebase.firestore.GeoPoint;

import java.util.*;

/**
 * Sorts an ambulance cache by distance from the current point.
 * This class encapsulates both the latest ambulance list and the latest location (in the
 * AmbulanceDistanceComparator), which are obtained from external sources updated asynchronously
 * and possibly even from separate threads (Firestore snapshots and FusedLocationProviderClient).
 */
public class AmbulanceSorter {
    private Map<String, Ambulance> ambulances;
    private List<Map.Entry<String,Ambulance>> ambulancesSorted = null;
    private AmbulanceDistanceComparator ambulanceDistanceComparator;

    /**
     * Create a new ambulance sorter
     */
    public AmbulanceSorter() {
        this.ambulances = null;
        this.ambulanceDistanceComparator = null;
    }

    /**
     * Create a new ambulance sorter
     * @param ambulances Map of String ids to Ambulance objects to sort
     * @param currentLocation Current location of device
     */
    public AmbulanceSorter(Map<String, Ambulance> ambulances, GeoPoint currentLocation) {
        this.ambulances = ambulances;
        this.ambulanceDistanceComparator = new AmbulanceDistanceComparator(currentLocation);
    }

    /*
     * ...making these methods synchronized has two effects:
     *
     * First, it is not possible for two invocations of synchronized methods on the same object to
     * interleave. When one thread is executing a synchronized method for an object, all other
     * threads that invoke synchronized methods for the same object block (suspend execution) until
     * the first thread is done with the object.
     *
     * Second, when a synchronized method exits, it automatically establishes a happens-before
     * relationship with any subsequent invocation of a synchronized method for the same object.
     * This guarantees that changes to the state of the object are visible to all threads.
     *
     * https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html
     *
     * Basically, threads won't try and sort while another is updating the location or something
     * crazy like that.
     */

    /**
     * Return a sorted list of id-ambulance pairs. Tries to use the cached location
     * This returns a shallow copy of the unsorted ambulance cache passed in by updateAmbulances()
     * or the constructor. Don't modify the ambulance objects in the sorted list
     * @return Sorted list of id-ambulances pairs
     */
    public synchronized List<Map.Entry<String, Ambulance>> getAmbulancesSorted() {
        if (ambulancesSorted == null) {
            // Shallow copy is ok, as long as ambulance objects aren't modified when the sorted list is
            // returned to the caller
            if (ambulances == null) {
                throw new NullPointerException("ambulances is null");
            }

            if (ambulanceDistanceComparator == null) {
                throw new NullPointerException("ambulanceDistanceComparator is null");
            }

            ambulancesSorted = new ArrayList<Map.Entry<String, Ambulance>>(ambulances.entrySet());
            Collections.sort(ambulancesSorted, ambulanceDistanceComparator);
        }

        return ambulancesSorted;
    }

    /**
     * Just get the nearest ambulance
     * @return Nearest id-ambulance pair
     */
    public Map.Entry<String, Ambulance> getNearestAmbulance() {
        return getAmbulancesSorted().get(0);
    }

    /**
     * Update the current location, invalidating the sorted ambulance list.
     * @param currentLocation Current location
     */
    public synchronized void updateLocation(GeoPoint currentLocation) {
        // updating the current location will make the current sorted ambulance list stale
        ambulancesSorted = null;
        ambulanceDistanceComparator = new AmbulanceDistanceComparator(currentLocation);
    }

    /**
     * Update the collection of ambulances, invalidating the sorted ambulance list.
     * @param ambulances New ambulances
     */
    public synchronized void updateAmbulances(Map<String, Ambulance> ambulances) {
        // Don't actually want to throw away the current location
        ambulancesSorted = null;
        this.ambulances = ambulances;
    }
}
