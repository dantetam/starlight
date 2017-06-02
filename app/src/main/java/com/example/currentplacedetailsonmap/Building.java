package com.example.currentplacedetailsonmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/1/2017.
 */
public class Building implements Serializable {

    public int id;
    public String name;

    public List<Item> items;

    public Building(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Building(Building building) {
        this(building.id, building.name);
        items = new ArrayList<>();
    }

}
