package io.github.dantetam.quests;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import io.github.dantetam.util.GeoUtil;

/**
 * Created by Dante on 6/17/2017.
 *
 * The objective: stay around a particular set location for a certain number of ticks
 */
public class WalkVisitOverworldQuest extends OverworldQuest {

    private static final int WITHIN_QUEST_RANGE = 300;
    private static final int METERS_DISTINCT = 5;

    private int distTravelled, distNeeded;
    private LatLng goalLocation;
    private LatLng prevLocation;

    public WalkVisitOverworldQuest(String name, String desc, int distNeeded, LatLng goalLocation) {
        super(name, desc);
        this.distTravelled = 0;
        this.distNeeded = distNeeded;
        this.goalLocation = goalLocation;
    }

    @Override
    public void tick(Location location) {
        if (prevLocation == null) {

        }
        else {
            if (GeoUtil.calculateDistance(location.getLatitude(), location.getLongitude(), goalLocation.latitude, goalLocation.longitude) <= WITHIN_QUEST_RANGE) {
                distTravelled += Math.ceil(GeoUtil.calculateDistance(location.getLatitude(), location.getLongitude(), prevLocation.latitude, prevLocation.longitude));
            }
            else {
                distTravelled = 0;
            }
        }
        prevLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public boolean doneCondition() {
        return distTravelled >= distNeeded;
    }

    @Override
    public void onQuestCompletion() {}

}
