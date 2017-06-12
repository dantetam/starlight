package io.github.dantetam.xml;

import io.github.dantetam.world.Building;
import io.github.dantetam.world.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dante on 6/1/2017.
 *
 * This class is unique for every user and stores a possible set of items and buildings and skills
 * TODO: Items should be standardized across one class
 * This means that different people could have different types of buildings and such.
 */
public class ConstructionTree implements Serializable {

    private Map<Integer, Item> itemsById;
    private Map<Integer, Building> buildingsById;

    private Map<String, Item> itemsByName;
    private Map<String, Building> buildingsByName;

    private Map<Integer, Building> upgradedBuildingsById;
    private Map<String, Building> upgradedBuildingsByName;

    //private Map<String, List<String>> customResourceGroups;
    //public List<String> getCustomGroupsNames() {return new ArrayList<>(customResourceGroups.keySet());}

    public List<String> skills;

    public ConstructionTree() {
        itemsById = new HashMap<>();
        buildingsById = new HashMap<>();
        itemsByName = new HashMap<>();
        buildingsByName = new HashMap<>();
        upgradedBuildingsById = new HashMap<>();
        upgradedBuildingsByName = new HashMap<>();
        skills = new ArrayList<>();
        //customResourceGroups = new HashMap<>();
    }

    public void insertBaseBuilding(Building building) {
        buildingsById.put(building.id, building);
        buildingsByName.put(building.name, building);
    }

    public void insertUpgradeBuilding(Building building) {
        upgradedBuildingsById.put(building.id, building);
        upgradedBuildingsByName.put(building.name, building);
    }

    public void insertItem(Item item) {
        itemsById.put(item.id, item);
        itemsByName.put(item.name, item);
    }

    public Building getBuildingById(int id) {
        return buildingsById.get(id);
    }

    public Item getItemById(int id) {
        return itemsById.get(id);
    }

    public Building getBuildingByName(String name) {
        Building base = buildingsByName.get(name);
        if (base != null) {
            return base;
        }
        else {
            Building upgrade = upgradedBuildingsByName.get(name);
            return upgrade;
        }
    }

    public Item getItemByName(String name) {
        return itemsByName.get(name);
    }

    public Building copyBuilding(String name) {
        return new Building(getBuildingByName(name));
    }

    public Item copyItem(String name, int quantity) {
        Item original = getItemByName(name);
        if (original == null) {
            throw new IllegalArgumentException("In the stored item data, could not find item of name: " + name);
        }
        return new Item(original, quantity);
    }

    public Collection<Building> getAllBuildings() {
        return buildingsById.values();
    }

    /*public boolean itemIsInGroup(String group, String name) {
        if (!customResourceGroups.containsKey(group)) {
            throw new IllegalArgumentException("Could not find in item group name: " + group);
        }
        else {
            List<String> groupItems = customResourceGroups.get(group);
            return groupItems.contains(name);
        }
    }*/

}
