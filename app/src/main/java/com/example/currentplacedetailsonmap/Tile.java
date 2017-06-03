package com.example.currentplacedetailsonmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 5/18/2017.
 */
public class Tile implements Serializable {

    public int tileType;
    public int row, col;
    private Building building;
    public List<Item> resources;

    public Tile(int r, int c) {
        row = r;
        col = c;
        resources = new ArrayList<>();
    }

    public void addBuilding(Building newBuilding) {
        this.building = newBuilding;
    }

    public Building getBuilding() {
        return building;
    }

}
