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
    public String desc;

    public String resourceNeeded;

    public int housingNum;

    public List<Item> items;
    public int resourceLimit;

    private List<Recipe> cost;
    private List<Recipe> productions;

    public Building(int id, String name, String desc, int resourceLimit, int housingNum, String resourceNeeded) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.resourceLimit = resourceLimit;
        this.housingNum = housingNum;
        this.resourceNeeded = resourceNeeded;
    }

    public Building(Building building) {
        this(building.id, building.name, building.desc, building.resourceLimit, building.housingNum, building.resourceNeeded);
        items = new ArrayList<>();
        cost = new ArrayList<>();
        productions = new ArrayList<>();
    }

    public void addBuildingCost(Recipe recipe) {
        cost.add(recipe);
    }

    public void addProductionRecipe(Recipe recipe) {
        productions.add(recipe);
    }

}
