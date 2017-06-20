package io.github.dantetam.quests;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import io.github.dantetam.world.Inventory;

/**
 * Created by Dante on 6/17/2017.
 */
public abstract class OverworldQuest implements Serializable {

    public static final int METERS_INTERACT = 1000;

    public String name, desc;

    public Inventory reward;

    public OverworldQuest(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public abstract void tick(Location location); //Do something every tick if necessary
    public abstract boolean doneCondition(); //Checked every certain number of ticks
    public abstract void onQuestCompletion(); //Intended to execute upon completing a quest
    public Inventory getReward() {
        return reward;
    }; //Intended to be rewarded to a player upon completion

}
