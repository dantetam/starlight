package io.github.dantetam.maps;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.dantetam.util.GeoUtil;

/**
 * Created by Dante on 6/21/2017.
 */
public class MapHistory {

    public static final int METERS_DISTINCT_LOCATION = 8; //Used for separating small locations within the same "place"
    public static final int METERS_DISTINCT_PLACE = 50; //Used for separating larger abstract places
    public int historyLength;

    private List<LatLng> historyLocations;
    private List<String> historyPlaceNames;
    private List<Date> historyDates;

    public MapHistory(int historyLength) {
        historyLocations = new ArrayList<>();
        historyPlaceNames = new ArrayList<>();
        historyDates = new ArrayList<>();
        this.historyLength = historyLength;
    }

    public List<LatLng> getHistoryInOrder() {
        return historyLocations;
    }

    public void addData(LatLng addLatLng, String placeName, Date timeAtLocation) {
        LatLng mostRecent = null;
        if (historyLocations.size() > 0) {
            mostRecent = historyLocations.get(historyLocations.size() - 1);
        }
        if (mostRecent == null || GeoUtil.calculateDistance(mostRecent, addLatLng) >= METERS_DISTINCT_LOCATION) {
            historyLocations.add(addLatLng);

            //SimpleDateFormat df = new SimpleDateFormat("MM dd yyyy");
            //String formattedDate = df.format(timeAtLocation);
            historyPlaceNames.add(placeName);
            historyDates.add(timeAtLocation);

            if (historyLocations.size() > historyLength) {
                historyLocations.remove(0);
                historyDates.remove(0);
            }
        }
    }

    public PlaceSummary getPlacesSummary() { //TODO: Use this in the main map activity UI
        List<LatLng> resultLocations = new ArrayList<>();
        List<String> resultNames = new ArrayList<>();
        List<Date> resultDates = new ArrayList<>();
        LatLng anchor = null;
        for (int i = 0; i < historyDates.size(); i++) {
            LatLng latLng = historyLocations.get(i);
            String placeName = historyPlaceNames.get(i);
            Date date = historyDates.get(i);
            if (anchor == null || GeoUtil.calculateDistance(anchor, latLng) >= METERS_DISTINCT_PLACE) {
                anchor = latLng;
                resultLocations.add(latLng);
                resultNames.add(placeName);
                resultDates.add(date);
            }
        }
        return new PlaceSummary(resultLocations, resultNames, resultDates);
    }

    public class PlaceSummary {
        public List<LatLng> locations;
        public List<String> placeNames;
        public List<Date> dates;
        public int size;
        public PlaceSummary(List<LatLng> locations, List<String> placeNames, List<Date> dates) {
            if (locations.size() != dates.size()) {
                throw new IllegalArgumentException("Map history data not aligned in pairs");
            }
            size = locations.size();
            this.locations = locations;
            this.placeNames = placeNames;
            this.dates = dates;
        }
    }

}
