package io.github.dantetam.world;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dante on 5/18/2017.
 */
public class Item implements Serializable {

    public int id;
    public int quantity;
    public String name;
    public int health, maxHealth;
    public String quality;
    public Integer superClassId = null;
    public float baseMarketPrice;

    private Map<String, Float> itemData;
    public float getItemData(String name) {
        if (itemData.containsKey(name)) {
            return itemData.get(name);
        }
        else {
            throw new IllegalArgumentException("Could not find name of key: " + name);
        }
    }
    public void putItemData(String key, Float value) {
        itemData.put(key, value);
    }
    public boolean hasItemData(String name) {return itemData.containsKey(name);}

    public Item(int id, String name, int maxHealth, float baseMarketPrice) {
        this.id = id;
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.itemData = new HashMap<>();
        this.baseMarketPrice = baseMarketPrice;
    }

    public Item(Item item, int quantity) {
        this(item.id, item.name, item.maxHealth, item.baseMarketPrice);
        for (Map.Entry<String, Float> entry: item.itemData.entrySet()) {
            this.putItemData(entry.getKey(), entry.getValue());
        }
        this.superClassId = item.superClassId;
        if (quantity == 0) {
            throw new IllegalArgumentException("Cannot create a item of zero quantity");
        }
        this.quantity = quantity;
    }

    public String toString() {
        return quantity + " " + name;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Item)) {
            return false;
        }
        Item item = (Item) other;
        return this.id == item.id && this.quantity == item.quantity && this.health == item.health;
    }

}
