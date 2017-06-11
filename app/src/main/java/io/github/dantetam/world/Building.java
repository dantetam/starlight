package io.github.dantetam.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dante on 6/1/2017.
 */
public class Building implements Serializable {

    public int id;
    public String name;
    public String desc;

    public String resourceNeeded;

    public int housingNum;

    public Inventory items;
    public int resourceLimit;

    private List<Recipe> cost;
    private List<Recipe> productions;
    public int costRecipeNum = 0;

    private String jobType;
    public String getJobType() {return jobType;}

    public List<String> possibleBuildingUpgrades;
    public List<String> currentUpgrades;

    public int maxRecipesEnabled = 1;
    public int getMaxRecipesEnabled() {return maxRecipesEnabled;}
    private List<Integer> activeRecipes;

    private Tile tile;
    public Tile getTile() {return tile;}
    public void setTile(Tile t) {tile = t;}

    private Map<String, Float> buildingData;
    public float getBuildingData(String name) {
        if (buildingData.containsKey(name)) {
            return buildingData.get(name);
        }
        else {
            throw new IllegalArgumentException("Could not find name of key: " + name);
        }
    }
    public void putBuildingData(String key, Float value) {
        buildingData.put(key, value);
    }

    public int buildTime;

    public Building(int id, String name, String desc, int resourceLimit, int housingNum, String resourceNeeded, String jobType, int buildTime) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.resourceLimit = resourceLimit;
        this.housingNum = housingNum;
        this.resourceNeeded = resourceNeeded;
        this.jobType = jobType;
        this.buildTime = buildTime;
        this.buildingData = new HashMap<>();
        items = new Inventory();
        cost = new ArrayList<>();
        productions = new ArrayList<>();
        possibleBuildingUpgrades = new ArrayList<>();
        currentUpgrades = new ArrayList<>();
    }

    public Building(Building building) {
        this(building.id, building.name, building.desc, building.resourceLimit, building.housingNum, building.resourceNeeded, building.jobType, building.buildTime);
        for (Map.Entry<String, Float> entry: building.buildingData.entrySet()) {
            this.putBuildingData(entry.getKey(), entry.getValue());
        }
        items = new Inventory();
        cost = new ArrayList<>(building.cost);
        productions = new ArrayList<>(building.productions);
        activeRecipes = new ArrayList<>();
        activeRecipes.add(0);
        possibleBuildingUpgrades = new ArrayList<>(building.possibleBuildingUpgrades);
        currentUpgrades = new ArrayList<>(building.currentUpgrades);
    }

    public void addBuildingCost(Recipe recipe) {
        cost.add(recipe);
    }

    public void addProductionRecipe(Recipe recipe) {
        productions.add(recipe);
    }

    public List<Recipe> getCostRecipes() {
        return cost;
    }

    public List<Recipe> getProductionRecipes() {
        return productions;
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
        return items.toString();
    }

    //Account for the special "Resource" which simply refers to the resource within the tile
    public void produce() {
        if (productions.size() == 0) return;
        List<Integer> successfulRecipes = new ArrayList<>();
        for (int recipeIndex: activeRecipes) {
            Recipe recipe = productions.get(recipeIndex);
            if (this.items.hasInventory(recipe.input)) {
                this.items.removeInventory(recipe.input);
                successfulRecipes.add(recipeIndex);
            }
        }
        for (int recipeIndex: successfulRecipes) {
            Recipe recipe = productions.get(recipeIndex);
            this.items.addInventory(recipe.output);
        }
        //The only items of temporary type "Resource" must be added here
        //i.e. all "Resource" items are converted to the same object
        if (tile != null) {
            //if (tile.resources.getItems().size() > 0) {
            this.items.replaceGenericTileResource(tile.resources.getItems());
            //}
        }
    }

    public void activateRecipe(int index) {
        if (!activeRecipes.contains(index) && activeRecipes.size() < maxRecipesEnabled) {
            activeRecipes.add(new Integer(index));
        }
    }

    public void deactivateRecipe(int index) {
        if (activeRecipes.contains(index)) {
            activeRecipes.remove(new Integer(index));
        }
    }

    public void deactivateRandom() {
        int index = (int) (Math.random() * activeRecipes.size());
        activeRecipes.remove(index);
    }

    public List<Integer> getActiveRecipes() {
        return activeRecipes;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Building)) {
            return false;
        }
        Building building = (Building) other;
        return this.id == building.id;
    }

}
