package com.example.currentplacedetailsonmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/3/2017.
 *
 * A custom class which encapsulates a list of actions,
 * it has special methods for additions, deletions, etc.
 *
 * Test cases:
 *
 * Adding items together of same id
 * Adding items of different health
 * Removing inventories of items
 */
public class Inventory implements Serializable {

    private List<Item> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public Inventory(List<Item> items) {
        this.items = items;
    }

    public static Inventory copyInventory(Inventory other) {
        Inventory clone = new Inventory();
        for (Item item: other.items) {
            clone.addItem(new Item(item, item.quantity));
        }
        return clone;
    }

    public String toString() {
        if (items.size() == 0) {
            return "Empty";
        }
        String result = "";
        for (Item item: items) {
            result += item.toString() + ", ";
        }
        return result.substring(0, result.length() - 2);
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            Item heldItem = items.get(i);
            if (heldItem.id == item.id && heldItem.health == item.health) {
                heldItem.quantity += item.quantity;
                return;
            }
        }
        items.add(item);
    }
    public void addInventory(Inventory other) {
        for (Item item: other.items) {
            addItem(item);
        }
    }

    public boolean hasItem(Item item) {
        return findItem(item) != null;
    }
    public Inventory findItem(Item item) {
        int desired = item.quantity;
        Inventory result = new Inventory();
        for (int i = 0; i < items.size(); i++) {
            Item heldItem = items.get(i);
            if (heldItem.id == item.id) {
                desired -= heldItem.quantity;
                Item foundItem = new Item(heldItem, heldItem.quantity);
                if (desired < 0) {
                    foundItem.quantity -= desired;
                }
                result.addItem(foundItem);
            }
        }
        if (desired <= 0) {
            return result;
        }
        return null;
    }

    public void removeItem(Item item) {
        Inventory foundItems = findItem(item);
        if (foundItems == null) {
            return;
        }

        int desired = item.quantity;
        for (int i = items.size() - 1; i >= 0; i--) {
            Item heldItem = items.get(i);
            if (heldItem.id == item.id) {
                desired -= heldItem.quantity;
                if (desired < 0) {
                    heldItem.quantity = -desired;
                }
                else {
                    items.remove(i);
                }
            }
        }
    }

    public boolean hasInventory(Inventory other) {
        Inventory temp = Inventory.copyInventory(this);
        for (Item otherItem: other.items) {
            if (temp.hasItem(otherItem)) {
                temp.removeItem(otherItem);
            }
            else {
                return false;
            }
        }
        return true;
    }

    public void removeInventory(Inventory other) {
        for (Item item: other.items) {
            removeItem(item);
        }
    }

    public boolean findAndRemoveInventory(Inventory other) {
        if (!hasInventory(other)) {
            return false;
        }
        else {
            removeInventory(other);
            return true;
        }
    }

    public int size() {
        return items.size();
    }

    /*public void subtractInventory(Inventory other) {
        if (hasInventory(other)) {
            for (Item item: other.items) {
                removeItem(item);
            }
        }
    }*/

}
