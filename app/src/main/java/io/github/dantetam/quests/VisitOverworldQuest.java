package io.github.dantetam.quests;

import android.location.Location;

/**
 * Created by Dante on 6/17/2017.
 *
 * The objective: stay around a particular set location for a certain number of ticks
 */
public class VisitOverworldQuest extends OverworldQuest {

    private static final int METERS_CLOSE = 50;

    private int ticksReceieved, ticksNeeded;
    private Location goalLocation;

    public VisitOverworldQuest(String name, String desc, int ticksNeeded, Location goalLocation) {
        super(name, desc);
        this.ticksReceieved = 0;
        this.ticksNeeded = ticksNeeded;
        this.goalLocation = goalLocation;
    }

    @Override
    public void tick(Location location) {
        if (location.distanceTo(goalLocation) <= METERS_CLOSE) {
            ticksReceieved++;
        }
        else {
            ticksReceieved = 0;
        }
    }

    @Override
    public boolean doneCondition() {
        return ticksReceieved >= ticksNeeded;
    }

    @Override
    public void onQuestCompletion() {}

}
