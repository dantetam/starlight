package io.github.dantetam.quests;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dante on 6/22/2017.
 */
public class QuestFactory {

    public static OverworldQuest randomQuest(LatLng location) {
        double rand = Math.random();
        OverworldQuest quest;
        if (rand < 0.25) {
            quest = new PictureOverworldQuest("Picture Quest", "Take a picture near the following location.", 0, location);
            quest.omnigold = (int) (Math.random() * 15) + 10;
        }
        else if (rand < 0.5) {
            quest = new FreeOverworldQuest("Free Quest", "Travel to this location.");
            quest.omnigold = (int) (Math.random() * 5) + 5;
        }
        else if (rand < 0.75) {
            quest = new VisitOverworldQuest("Check-in Quest", "Travel to this location and stay for some time.", 50 * 60, location);
            quest.omnigold = (int) (Math.random() * 10) + 5;
        }
        else {
            quest = new WalkVisitOverworldQuest("Walking Quest", "Walk around this location.", 50, location);
            quest.omnigold = (int) (Math.random() * 15) + 10;
        }
        return quest;
    }

}
