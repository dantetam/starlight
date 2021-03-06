package io.github.dantetam.world;

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
        items.add(new Item(item, item.quantity));
    }
    public void addInventory(Inventory other) {
        for (Item item: other.items) {
            addItem(item);
        }
    }
    public void addItem(Item... items) {
        for (Item item: items) {
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
            if (heldItem.id == item.id || (heldItem.superClassId != null && heldItem.superClassId == item.id)) {
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
            if (heldItem.id == item.id || (heldItem.superClassId != null && heldItem.superClassId == item.id)) {
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

    public boolean hasNutrition(float nutrition) {
        return findNutrition(nutrition) != null;
    }
    public Inventory findNutrition(float nutrition) {
        Inventory result = new Inventory();
        for (int i = 0; i < items.size(); i++) {
            Item heldItem = items.get(i);
            if (heldItem.hasItemData("nutrition")) {
                int eatQuantity = Math.min(heldItem.quantity, (int) Math.ceil(nutrition / heldItem.getItemData("nutrition")));
                nutrition -= eatQuantity * heldItem.getItemData("nutrition");
                Item foundItem = new Item(heldItem, eatQuantity);
                /*if (nutrition < 0) {
                    foundItem.quantity -= nutrition / heldItem.getItemData("nutrition");
                }*/
                result.addItem(foundItem);
                if (nutrition <= 0) {
                    return result;
                }
            }
        }
        return null;
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

    //Exchange all units of "Resource" for a set of replacement items
    public void replaceGenericTileResource(List<Item> replacements) {
        int multiple = 0;
        for (int i = items.size() - 1; i >= 0; i--) {
            Item possibleGeneric = items.get(i);
            if (possibleGeneric.name.equals("Resource")) {
                multiple += possibleGeneric.quantity;
                items.remove(i);
            }
        }
        if (multiple > 0) {
            for (Item replacement: replacements) {
                addItem(new Item(replacement, replacement.quantity * multiple));
            }
        }
    }

}
