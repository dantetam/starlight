package com.example.currentplacedetailsonmap;

/**
 * Created by Dante on 5/18/2017.
 */
public class Item {

    public int itemType;
    public String name;
    public int health, maxHealth;

    public Item(int type, String name, int maxHealth) {
        this.itemType = type;
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

}
