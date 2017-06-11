package io.github.dantetam.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.github.dantetam.world.Inventory;

/**
 * Created by Dante on 6/9/2017.
 *
 * A 2d grid overlay over the real world, this class represents a "rectangle"
 * of total geo coordinate width and height equal to (totalGeoLat, totalGeoLon).
 * It is subdivided into a predetermined number of slices,
 * and the user's location within this overlay is determined for the resources given to the settlement.
 */
public class MapResourceOverlay {

    public LatLng geoCenter;
    public float totalGeoLat, totalGeoLon;

    public byte[][] resourceData;
    public List<Inventory> inventoryList;

    public MapResourceOverlay(LatLng geoCenter, int divLat, int divLon, byte[][] resourceData, List<Inventory> inventoryList) {
        if (divLat <= 0 || divLon <= 0) {
            throw new IllegalArgumentException("Geo coord divisions must be greater than 0.");
        }
        if (resourceData.length != resourceData.length || resourceData[0].length != resourceData[0].length) {
            throw new IllegalArgumentException("Initialization of map resources, data mismatch.");
        }
        this.geoCenter = geoCenter;
        this.resourceData = resourceData;
        this.inventoryList = inventoryList;
    }

    public void initializeResourcesData() {

        /*for (int r = 0; r < resourceData.length; r++) {
            for (int c = 0; c < resourceData[0].length; c++) {
                int index = data[r][c];
                if (index < 0) {
                    index = 0;
                }
                else if (index >= inventoryList.size()) {
                    index = inventoryList.size() - 1;
                }
                resourcesInGeoTiles[r][c] = inventoryList.get(index);
            }
        }*/
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
        float divLat = totalGeoLat / resourceData.length;
        float divLon = totalGeoLon / resourceData[0].length;
        int r = (int) Math.floor((geoCoord.latitude - (geoCenter.latitude - totalGeoLat / 2.0)) / divLat);
        int c = (int) Math.floor((geoCoord.longitude - (geoCenter.longitude - totalGeoLon / 2.0)) / divLon);
        byte index = resourceData[r][c];
        if (index < 0) {
            index = 0;
        }
        else if (index >= inventoryList.size()) {
            index = (byte) (inventoryList.size() - 1);
        }
        return inventoryList.get(index);
    }

}
