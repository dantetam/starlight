package io.github.dantetam.world;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dantetam.person.Faction;
import io.github.dantetam.person.Person;
import io.github.dantetam.quests.OverworldQuest;
import io.github.dantetam.util.Vector2f;

/**
 * Created by Dante on 6/17/2017.
 */
public class QuestLocation {

    public Vector2f realGeoCoord, gameCoord;
    public List<Person> people;

    public String name;
    public Date foundDate;
    public String formattedDate;

    public Faction faction;

    public List<OverworldQuest> quests;

    public QuestLocation(String name, Date foundDate, Vector2f realGeoCoord, Vector2f gameCoord, Faction faction) {
        this.name = name;
        this.foundDate = foundDate;
        this.realGeoCoord = realGeoCoord;
        this.gameCoord = gameCoord;
        this.faction = faction;
        people = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("MM dd yyyy");
        formattedDate = df.format(foundDate);
    }

}
