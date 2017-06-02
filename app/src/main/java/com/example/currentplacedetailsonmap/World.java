package com.example.currentplacedetailsonmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import terrain.DiamondSquare;

/**
 * Created by Dante on 5/18/2017.
 */
public class World {

    public List<Settlement> settlements;
    public static float MIN_SETTLEMENT_GEO_DIST;

    public static Vector2f geoHome;

    public World() {
        settlements = new ArrayList<Settlement>();
    }

    private boolean canCreateSettlement(Vector2f geoCoord) {
        boolean allowed = true;
        for (Settlement settlement : settlements) {
            allowed = allowed && settlement.realGeoCoord.dist(geoCoord) >= MIN_SETTLEMENT_GEO_DIST;
        }
        return allowed;
    }

    public Settlement createSettlement(String name, Date foundDate, Vector2f geoCoord) {
        if (canCreateSettlement(geoCoord)) {
            Settlement settlement = new Settlement(name, foundDate, geoCoord, convertToGameCoord(geoCoord), 20, 20);
            settlement.initializeSettlement(generateTiles(20, 20));
            settlements.add(settlement);
            return settlement;
        }
        else
            return null;
    }

    public Vector2f convertToGameCoord(Vector2f geoCoord) {
        Vector2f geoDist = Vector2f.sub(geoCoord, geoHome);
        return new Vector2f(geoDist.x * 100.0f, geoDist.y * 100.0f);
    }

    public int[][] generateTiles(int width, int height) {
        int larger = Math.max(width, height);
        int sufficientWidth = (int)Math.pow(2, (int)Math.ceil(Math.log(larger)/Math.log(2)));
        double[][] temp = DiamondSquare.makeTable(3, 3, 3, 3, sufficientWidth + 1);
        DiamondSquare ds = new DiamondSquare(temp);
        ds.seed(System.currentTimeMillis());
        return convertToIntArray(ds.generate(new double[]{0, 0, sufficientWidth, 2, 0.4}), width, height);
    }

    private int[][] convertToIntArray(double[][] arr, int desiredWidth, int desiredHeight) {
        if (arr.length == 0) return new int[0][0];
        if (desiredWidth > arr.length || desiredHeight > arr[0].length) {
            throw new IllegalArgumentException("Can't create an array subset greater than original array");
        }
        int[][] result = new int[desiredWidth][desiredHeight];
        for (int r = 0; r < desiredWidth; r++) {
            for (int c = 0; c < desiredHeight; c++) {
                result[r][c] = (int) arr[r][c];
            }
        }
        return result;
    }

}
