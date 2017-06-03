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
        items = new ArrayList<>();
        cost = new ArrayList<>();
        productions = new ArrayList<>();
    }

    public Building(Building building) {
        this(building.id, building.name, building.desc, building.resourceLimit, building.housingNum, building.resourceNeeded);
        items = new ArrayList<>(building.items);
        cost = new ArrayList<>(building.cost);
        productions = new ArrayList<>(building.productions);
    }

    public void addBuildingCost(Recipe recipe) {
        cost.add(recipe);
    }

    public void addProductionRecipe(Recipe recipe) {
        productions.add(recipe);
    }

    public String getBuildingCostString() {
        if (cost.size() == 0) {
            return "None";
        }
        String result = "";
        for (Recipe recipe: cost) {
            result += recipe.toString() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }

    public String getProductionRecipeString() {
        if (productions.size() == 0) {
            return "Nothing";
        }
        String result = "";
        for (Recipe recipe: productions) {
            result += recipe.toString() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }

    private static final int ITEMS_TO_LIST = 10;
    public String getItemsString() {
        if (items.size() == 0) {
            return "Empty";
        }
        String result = "";
        int numItems = Math.min(items.size(), ITEMS_TO_LIST);
        for (int i = 0; i < numItems; i++) {
            result += items.get(i).toString() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }

    //TODO: Remember to account for the special "Resource" which simply refers to the resource within the tile
    public void produce() {

    }

}
