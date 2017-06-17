package io.github.dantetam.quests;

import android.location.Location;

/**
 * Created by Dante on 6/17/2017.
 */
public class FreeOverworldQuest extends OverworldQuest {

    public FreeOverworldQuest(String name, String desc) {
        super(name, desc);
    }

    @Override
    public void tick(Location location) {

    }

    @Override
    public boolean doneCondition() {
        return true;
    }

}
