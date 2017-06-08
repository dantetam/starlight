package com.example.currentplacedetailsonmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 5/18/2017.
 */
public class Tile implements Serializable, Traversable<Tile> {

    public int tileType;
    public int row, col;
    private Building building;
    public Inventory resources;
    public List<Person> people;
    public List<Tile> storedNeighbors;

    public Tile(int r, int c) {
        row = r;
        col = c;
        resources = new Inventory();
        people = new ArrayList<>();
        storedNeighbors = new ArrayList<>();
    }

    public void addBuilding(Building newBuilding) {
        this.building = newBuilding;
        this.building.setTile(this);
    }

    public void removeBuilding() {
        this.building.setTile(null);
        this.building = null;
    }

    public Building getBuilding() {
        return building;
    }

    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    public boolean accessible() {
        return true;
    }

    @Override
    public float dist(Tile tile) {
        if (row == tile.row || col == tile.col) {
            return 1;
        }
        return 1.1f;
    }

    @Override
    public List<Tile> neighbors() {
        return storedNeighbors;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tile)) {
            return false;
        }
        Tile tile = (Tile) other;
        return this.row == tile.row && this.col == tile.col;
    }
}
