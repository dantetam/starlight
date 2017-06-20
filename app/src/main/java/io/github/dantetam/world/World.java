package io.github.dantetam.world;

import android.location.Location;

import io.github.dantetam.jobs.CombatJob;
import io.github.dantetam.person.Faction;
import io.github.dantetam.quests.OverworldQuest;
import io.github.dantetam.xml.ConstructionTree;
import io.github.dantetam.util.GeoUtil;
import io.github.dantetam.util.MapUtil;
import io.github.dantetam.util.Vector2f;
import io.github.dantetam.jobs.Job;
import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.Task;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dantetam.terrain.DiamondSquare;

/**
 * Created by Dante on 5/18/2017.
 */
public class World implements Serializable {

    public String name;

    public List<Settlement> settlements;
    public ConstructionTree tree;
    public static float MIN_SETTLEMENT_GEO_DIST = 500; //In meters

    public List<QuestLocation> questLocations;

    public static Vector2f geoHome;

    public List<Faction> factions;

    public World(String name, ConstructionTree tree) {
        this.name = name;
        settlements = new ArrayList<>();
        questLocations = new ArrayList<>();
        this.tree = tree;
        factions = new ArrayList<>();
    }

    public void updateWorld() {
        //Look for jobs
        for (Settlement settlement: settlements) {
            for (Person person : settlement.people) {
                if (person.isDead()) {
                    continue;
                }
                //Assign a job, combat or non-combat
                if (person.isDrafted) {
                    Map<Person, Float> peopleByDist = new HashMap<>();
                    for (Person visitor: settlement.visitors) {
                        peopleByDist.put(visitor, person.tile.trueEuclideanDist(visitor.tile));
                    }

                    Map<Person, Float> sortedDists = MapUtil.sortByValueAscending(peopleByDist);
                    Collection<Person> sortedVisitors = sortedDists.keySet();

                    for (Person visitor: sortedVisitors) {
                        if (visitor.isDead()) {
                            continue;
                        }
                        if (factionsHostile(person.faction, visitor.faction)) {
                            Job attackJob = new CombatJob(settlement, person, visitor);
                            person.currentJob = attackJob;
                            //attackJob.reservedPerson = person;
                            break;
                        }
                    }
                }
                else if (person.currentJob == null) {
                    //Look for a job within the settlement based on priority
                    //Go through skills sorted by highest priority first,
                    //looking for available jobs within the respective settlements
                    //Map<String, Integer> sortedSkillsDescending = MapUtil.sortByValueDescending(person.skillPriorities);
                    findJobLoop:
                    for (Map.Entry<String, Integer> entry: person.getSortedSkillPrioritiesDes().entrySet()) {
                        String skillName = entry.getKey();
                        List<Job> jobsInSkill = settlement.availableJobsBySkill.get(skillName);
                        for (int i = 0; i < jobsInSkill.size(); i++) {
                            Job assignedJob = jobsInSkill.get(i);
                            if (assignedJob.reservedPerson == null) {
                                person.currentJob = assignedJob;
                                assignedJob.reservedPerson = person;
                                break findJobLoop;
                            }
                        }
                    }
                }

                //The person may or may not have been assigned a job
                if (person.currentJob != null) {
                    if (person.currentJob.doneCondition()) {
                        String skillName = person.currentJob.type();

                        //Find the job and remove it from any settlement level queues
                        if (settlement.availableJobsBySkill.get(skillName).contains(person.currentJob)) {
                            settlement.availableJobsBySkill.get(skillName).remove(person.currentJob);
                        }
                        else {
                            for (Map.Entry<String, Integer> entry: person.getSortedSkillPrioritiesDes().entrySet()) {
                                String otherSkillName = entry.getKey();
                                List<Job> jobsInSkill = settlement.availableJobsBySkill.get(otherSkillName);
                                jobsInSkill.remove(person.currentJob);
                            }
                        }

                        person.currentJob.reservedPerson = null;
                        person.currentJob = null;
                    }
                    else {
                        if (person.queueTasks.size() == 0) {
                            List<Task> newTasks = person.currentJob.createTasks();
                            for (Task newTask: newTasks) {
                                person.queueTasks.add(newTask);
                            }
                        }
                        else {
                            //Do nothing, the job is still going on.
                            //Later, process the person's queue of tasks,
                            //which may or may not be associated with the job.
                            //Note that a job does not override in progress/queued tasks
                        }
                    }
                }
            }
        }

        //Some test queued tasks
        /*for (Settlement settlement: settlements) {
            for (Person person : settlement.people) {
                if (person.queueTasks.size() == 0) {
                    Tile randDest = settlement.randomTile();
                    Task task = new MoveTask(10, person, settlement, randDest);
                    person.queueTasks.add(task);
                }
            }
        }*/

        for (Settlement settlement: settlements) {
            for (Person person: settlement.people) {
                if (person.queueTasks.size() > 0) {
                    Task task = person.queueTasks.get(0);
                    task.tick();
                    if (task.ticksLeft < 0) {
                        person.queueTasks.remove(0);
                    }
                }
            }
        }
    }

    public boolean canAccessQuestLocation(QuestLocation questLocation, Location location) {
        if (location == null) {
            return false;
        }
        if (GeoUtil.calculateDistance(
                questLocation.realGeoCoord.x,
                questLocation.realGeoCoord.y,
                location.getLatitude(),
                location.getLongitude()
        ) > OverworldQuest.METERS_INTERACT) {
            return false;
        }
        return true;
    }

    public void updateQuest(OverworldQuest quest, Location location) {
        quest.tick(location);
    }

    public void rewardQuest(QuestLocation questLocation, OverworldQuest quest) {
        quest.finishQuest();
        questLocation.heldItems.addInventory(quest.getReward());
    }

    public boolean canCreateSettlement(Vector2f geoCoord) {
        boolean allowed = true;
        for (Settlement settlement : settlements) {
            LatLng newCoord = new LatLng(geoCoord.x, geoCoord.y);
            LatLng settlementCoord = new LatLng(settlement.realGeoCoord.x, settlement.realGeoCoord.y);
            /*if (newCoord.equals(settlementCoord)) {
                continue;
            }*/
            //System.err.println(GeoUtil.calculateDistance(newCoord, settlementCoord));
            allowed = allowed && (GeoUtil.calculateDistance(newCoord, settlementCoord) >= MIN_SETTLEMENT_GEO_DIST);
        }
        return allowed;
    }

    public Settlement createSettlement(String name, Date foundDate, Vector2f geoCoord, Faction faction, ConstructionTree tree, Inventory possibleResources) {
        if (canCreateSettlement(geoCoord)) {
            int width = 26, height = 26;
            Settlement settlement = new Settlement(name, foundDate, geoCoord, convertToGameCoord(geoCoord), faction, width, height, tree);
            settlement.initializeSettlementTileTypes(generateTiles(width, height));

            /*possibleResources.add(tree.copyItem("Wood", 10));
            possibleResources.add(tree.copyItem("Iron", 10));
            //possibleResources.add(tree.copyItem("Food", 10));*/

            settlement.initializeSettlementTileResources(generateRandomTilesWithMask(width, height, 0, 10, 0.95f, -1), possibleResources.getItems());

            settlement.initializeNeighbors();

            settlements.add(settlement);
            return settlement;
        }
        else
            return null;
    }

    public List<Settlement> findNearbySettlements(Settlement home, float metersDist) {
        List<Settlement> results = new ArrayList<>();
        for (Settlement settlement: this.settlements) {
            if (GeoUtil.calculateDistance(new LatLng(settlement.realGeoCoord.x, settlement.realGeoCoord.y), new LatLng(home.realGeoCoord.x, home.realGeoCoord.y)) <= metersDist) {
                results.add(settlement);
            }
        }
        return results;
    }

    public QuestLocation createQuestLocation(String name, Date foundDate, Vector2f realGeoCoord, Faction faction) {
        QuestLocation questLocation = new QuestLocation(name, foundDate, realGeoCoord, convertToGameCoord(realGeoCoord), faction);
        questLocations.add(questLocation);
        return questLocation;
    }

    public Vector2f convertToGameCoord(Vector2f geoCoord) {
        Vector2f geoDist = Vector2f.sub(geoCoord, geoHome);
        return new Vector2f(geoDist.x * 100.0f, geoDist.y * 100.0f);
    }

    public static int[][] generateTiles(int width, int height) {
        int larger = Math.max(width, height);
        int sufficientWidth = (int)Math.pow(2, (int)Math.ceil(Math.log(larger)/Math.log(2)));
        double[][] temp = DiamondSquare.makeTable(4, 4, 4, 4, sufficientWidth + 1);
        DiamondSquare ds = new DiamondSquare(temp);
        ds.seed(System.currentTimeMillis());
        return convertToIntArray(ds.generate(new double[]{0, 0, sufficientWidth, 4, 0.55}), width, height);
    }

    //Generates random noise within min and max inclusive
    public int[][] generateRandomTiles(int width, int height, int min, int max) {
        int[][] result = new int[width][height];
        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                int randNumber = (int) Math.round(Math.random() * (max - min + 1) + min);
                result[r][c] = randNumber;
            }
        }
        return result;
    }

    //Generates random noise within min and max inclusive
    //and then takes a random proportion randomMask of numbers to set to the mask value
    //i.e. f(1,5,0,100,0.8,-1) -> [-1, -1, 87, -1, -1]
    public int[][] generateRandomTilesWithMask(int width, int height, int min, int max, float randomMask, int maskValue) {
        int[][] result = generateRandomTiles(width, height, min, max);
        int numToReplace = (int) (width * height * randomMask);
        int[] indices = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            indices[i] = i;
        }
        MapUtil.shuffleArray(indices);
        for (int i = 0; i < numToReplace; i++) {
            int indexToReplace = indices[i];
            int r = indexToReplace / height;
            int c = indexToReplace % height;
            result[r][c] = maskValue;
        }
        return result;
    }

    public static int[][] convertToIntArray(double[][] arr, int desiredWidth, int desiredHeight) {
        if (arr.length == 0) return new int[0][0];
        if (desiredWidth > arr.length || desiredHeight > arr[0].length) {
            throw new IllegalArgumentException("Can't create an array subset greater than original array");
        }
        int[][] result = new int[desiredWidth][desiredHeight];
        for (int r = 0; r < desiredWidth; r++) {
            for (int c = 0; c < desiredHeight; c++) {
                result[r][c] = (int) arr[r][c];
            }
        }
        return result;
    }

    public static byte[][] convertToByteArray(double[][] arr, int desiredWidth, int desiredHeight) {
        return convertToByteArray(convertToIntArray(arr, desiredWidth, desiredHeight) ,desiredWidth, desiredHeight);
    }
    public static byte[][] convertToByteArray(int[][] arr, int desiredWidth, int desiredHeight) {
        if (arr.length == 0) return new byte[0][0];
        if (desiredWidth > arr.length || desiredHeight > arr[0].length) {
            throw new IllegalArgumentException("Can't create an array subset greater than original array");
        }
        byte[][] result = new byte[desiredWidth][desiredHeight];
        for (int r = 0; r < desiredWidth; r++) {
            for (byte c = 0; c < desiredHeight; c++) {
                result[r][c] = (byte) arr[r][c];
            }
        }
        return result;
    }

    private boolean factionsHostile(Faction a, Faction b) {
        if (a.equals(b)) {
            return false;
        }
        return true;
    }

    public Faction getFaction(String name) {
        for (Faction faction: factions) {
            if (faction.name.equals(name)) {
                return faction;
            }
        }
        return null;
    }

    public Faction randomFaction() {
        int index = (int) (Math.random() * factions.size());
        return factions.get(index);
    }

}
