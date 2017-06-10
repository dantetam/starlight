package io.github.dantetam.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.github.dantetam.world.Inventory;

/**
 * Created by Dante on 6/9/2017.
 */
public class MapResourceOverlay {

    public LatLng geoCenter;
    public float totalGeoLat, totalGeoLon;

    public Inventory[][] resourcesInGeoTiles;

    public MapResourceOverlay(int divLat, int divLon) {
        if (divLat <= 0 || divLon <= 0) {
            throw new IllegalArgumentException("Geo coord divisions must be greater than 0.");
        }
        resourcesInGeoTiles = new Inventory[divLat][divLon];
        for (int r = 0; r < divLat; r++) {
            for (int c = 0; c < divLon; c++) {
                resourcesInGeoTiles[r][c] = new Inventory();
            }
        }
    }

    public void initializeResourcesData(int[][] data, List<Inventory> inventoryList) {
        if (data.length != resourcesInGeoTiles.length || data[0].length != resourcesInGeoTiles[0].length) {
            throw new IllegalArgumentException("Initialization of map resources, data mismatch.");
        }
        for (int r = 0; r < resourcesInGeoTiles.length; r++) {
            for (int c = 0; c < resourcesInGeoTiles[0].length; c++) {
                int index = data[r][c];
                if (index < 0) {
                    index = 0;
                }
                else if (index >= inventoryList.size()) {
                    index = inventoryList.size() - 1;
                }
                resourcesInGeoTiles[r][c] = inventoryList.get(index);
            }
        }
    }

    public boolean inBounds(LatLng geoCoord) {
        return geoCoord.latitude >= geoCenter.latitude - totalGeoLat / 2 &&
                geoCoord.latitude <= geoCenter.latitude + totalGeoLat / 2 &&
                geoCoord.longitude >= geoCenter.longitude - totalGeoLon / 2 &&
                geoCoord.longitude <= geoCenter.longitude + totalGeoLon / 2;
    }

    public Inventory getItemsAtCoord(LatLng geoCoord) {
        if (!inBounds(geoCoord)) {
            return null;
        }
        float divLat = totalGeoLat / resourcesInGeoTiles.length;
        float divLon = totalGeoLon / resourcesInGeoTiles[0].length;
        int r = (int) Math.floor((geoCoord.latitude - (geoCenter.latitude - totalGeoLat / 2.0)) / divLat);
        int c = (int) Math.floor((geoCoord.longitude - (geoCenter.longitude - totalGeoLon / 2.0)) / divLon);
        return resourcesInGeoTiles[r][c];
    }

}
