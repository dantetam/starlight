package com.example.currentplacedetailsonmap;

import java.io.Serializable;

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

    public Item(int id, String name, int maxHealth) {
        this.id = id;
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public Item(Item item, int quantity) {
        this(item.id, item.name, item.maxHealth);
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
