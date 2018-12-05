package com.example.kohseukim.clientside;

import com.google.firebase.firestore.GeoPoint;

import java.util.*;

/**
 * Sorts ambulances by distance from the current point.
 */
public class AmbulanceSorter {
    private List<Ambulance> ambulances;
    private List<Ambulance> ambulancesSorted = null;
    private AmbulanceDistanceComparator ambulanceDistanceComparator;

    /**
     * Create a new ambulance sorter
     * @param ambulances List of Ambulance objects to sort
     * @param currentLocation Current location of device
     */
    public AmbulanceSorter(List<Ambulance> ambulances, GeoPoint currentLocation) {
        this.ambulances = ambulances;
        this.ambulanceDistanceComparator = new AmbulanceDistanceComparator(currentLocation);
    }

    private void sort() {
        // Shallow copy is ok, as long as ambulance objects aren't modified when the sorted list is
        // returned to the caller
        ambulancesSorted = new ArrayList<>(ambulances);

        Collections.sort(ambulancesSorted, ambulanceDistanceComparator);
    }

    /**
     * Return a sorted list of ambulances. Tries to use the cached location
     * This returns a shallow copy of the unsorted ambulances list passed in by updateAmbulances()
     * or the constructor. Don't modify the ambulance objects in the sorted list
     * @return Sorted list of ambulances
     */
    public List<Ambulance> getAmbulancesSorted() {
        if (ambulancesSorted == null) {
            sort();
        }
        return ambulancesSorted;
    }

    private void invalidateSorted() {
        ambulancesSorted = null;
        ambulanceDistanceComparator = null;
    }

    public void updateLocation(GeoPoint currentLocation) {
        invalidateSorted();
        ambulanceDistanceComparator = new AmbulanceDistanceComparator(currentLocation);
    }

    public void updateAmbulances(List<Ambulance> ambulances) {
        ambulancesSorted = null;
        this.ambulances = ambulances;
    }
}
