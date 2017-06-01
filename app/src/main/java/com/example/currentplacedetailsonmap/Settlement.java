package com.example.currentplacedetailsonmap;

/**
 * Created by Dante on 5/18/2017.
 */
public class Settlement {

    public Vector2f realGeoCoord, gameCoord;
    public Tile[][] tiles;

    public Settlement(Vector2f realGeoCoord, Vector2f gameCoord, int r, int c) {
        this.realGeoCoord = realGeoCoord;
        this.gameCoord = gameCoord;
        tiles = new Tile[r][c];
    }

    public void initializeSettlement(int[][] resources) {
        if (resources.length != tiles.length || tiles.length == 0) {
            throw new IllegalArgumentException("Resources array not aligned with world or world is of size 0");
        }
        for (int r = 0; r < tiles.length; r++) {
            for (int c = 0; c < tiles[0].length; c++) {
                tiles[r][c] = new Tile(r,c);
                tiles[r][c].tileType = resources[r][c];
            }
        }
    }

}
