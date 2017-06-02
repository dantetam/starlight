package com.example.currentplacedetailsonmap;

import java.io.Serializable;

/**
 * Created by Dante on 5/18/2017.
 */
public class Tile implements Serializable {

    public int tileType;
    public int row, col;
    private Building building;

    public Tile(int r, int c) {
        row = r;
        col = c;
    }

    public void addBuilding(Building newBuilding) {
        this.building = newBuilding;
    }

}
