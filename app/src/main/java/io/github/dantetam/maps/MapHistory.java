package io.github.dantetam.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.util.GeoUtil;

/**
 * Created by Dante on 6/21/2017.
 */
public class MapHistory {

    public static int METERS_DISTINCT_LOCATION = 5;
    public int historyLength;

    private List<LatLng> historyLocations;

    public MapHistory(int historyLength) {
        historyLocations = new ArrayList<>();
        this.historyLength = historyLength;
    }

    public List<LatLng> getHistoryInOrder() {
        return historyLocations;
    }

    public void addData(LatLng addLatLng) {
        LatLng mostRecent = null;
        if (historyLocations.size() > 0) {
            mostRecent = historyLocations.get(historyLocations.size() - 1);
        }
        if (mostRecent == null || GeoUtil.calculateDistance(mostRecent, addLatLng) >= METERS_DISTINCT_LOCATION) {
            historyLocations.add(addLatLng);
            if (historyLocations.size() > historyLength) {
                historyLocations.remove(0);
            }
        }
    }

}
