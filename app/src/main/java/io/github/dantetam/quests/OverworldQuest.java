package io.github.dantetam.quests;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import io.github.dantetam.world.Inventory;

/**
 * Created by Dante on 6/17/2017.
 */
public abstract class OverworldQuest {

    public static final int METERS_INTERACT = 1000;

    public String name, desc;
    public Inventory reward;

    public OverworldQuest(String name, String desc) {
        this.name = name;
        this.desc = desc;
        reward = new Inventory();
    }

    public abstract void tick(Location location);
    public abstract boolean doneCondition();

}