package com.example.currentplacedetailsonmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/1/2017.
 */
public class Person implements Serializable {

    public String name;
    public Inventory items;

    public Person(String name) {
        this.name = name;
        this.items = new Inventory();
    }

    public String getItemsString() {
        return items.toString();
    }

}
