package com.example.currentplacedetailsonmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dante on 6/1/2017.
 */
public class ConstructionTree {

    private Map<Integer, Item> itemsById;
    private Map<Integer, Building> buildingsById;

    private Map<String, Item> itemsByName;
    private Map<String, Building> buildingsByName;

    public ConstructionTree() {
        itemsById = new HashMap<>();
        buildingsById = new HashMap<>();
        itemsByName = new HashMap<>();
        buildingsByName = new HashMap<>();
    }

    public void insertBuilding(Building building) {
        buildingsById.put(building.id, building);
        buildingsByName.put(building.name, building);
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
        return buildingsByName.get(name);
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

}
