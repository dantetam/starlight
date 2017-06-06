package com.example.currentplacedetailsonmap;

import com.example.currentplacedetailsonmap.jobs.Job;
import com.example.currentplacedetailsonmap.tasks.MoveTask;
import com.example.currentplacedetailsonmap.tasks.Task;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import terrain.DiamondSquare;

/**
 * Created by Dante on 5/18/2017.
 */
public class World {

    public List<Settlement> settlements;
    public ConstructionTree tree;
    public static float MIN_SETTLEMENT_GEO_DIST = 500; //In meters

    public static Vector2f geoHome;

    public World(ConstructionTree tree) {
        settlements = new ArrayList<Settlement>();
        this.tree = tree;
    }

    public void updateWorld() {
        //Look for jobs
        for (Settlement settlement: settlements) {
            for (Person person : settlement.people) {
                if (person.currentJob == null) {
                    //Look for a job within the settlement based on priority
                    //Go through skills sorted by highest priority first,
                    //looking for available jobs within the respective settlements
                    Map<String, Integer> sortedSkillsDescending = MapUtil.sortByValueDescending(person.skillPriorities);
                    findJobLoop:
                    for (Map.Entry<String, Integer> entry: sortedSkillsDescending.entrySet()) {
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
                            for (Map.Entry<String, Integer> entry: person.skillPriorities.entrySet()) {
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
                            //Do nothing, the job is still going open.
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

    private boolean canCreateSettlement(Vector2f geoCoord) {
        boolean allowed = true;
        for (Settlement settlement : settlements) {
            LatLng newCoord = new LatLng(geoCoord.x, geoCoord.y);
            LatLng settlementCoord = new LatLng(settlement.realGeoCoord.x, settlement.realGeoCoord.y);
            allowed = allowed && (GeoUtil.calculateDistance(newCoord, settlementCoord) >= MIN_SETTLEMENT_GEO_DIST);
        }
        return allowed;
    }

    public Settlement createSettlement(String name, Date foundDate, Vector2f geoCoord) {
        if (canCreateSettlement(geoCoord)) {
            Settlement settlement = new Settlement(name, foundDate, geoCoord, convertToGameCoord(geoCoord), 20, 20);
            settlement.initializeSettlementTileTypes(generateTiles(20, 20));

            List<Item> possibleResources = new ArrayList<>();
            possibleResources.add(tree.copyItem("Wood", 10));
            possibleResources.add(tree.copyItem("Iron", 10));
            //possibleResources.add(tree.copyItem("Food", 10));

            settlement.initializeSettlementTileResources(generateRandomTilesWithMask(20, 20, 0, 10, 0.8f, -1), possibleResources);
            settlements.add(settlement);
            return settlement;
        }
        else
            return null;
    }

    public Vector2f convertToGameCoord(Vector2f geoCoord) {
        Vector2f geoDist = Vector2f.sub(geoCoord, geoHome);
        return new Vector2f(geoDist.x * 100.0f, geoDist.y * 100.0f);
    }

    public static int[][] generateTiles(int width, int height) {
        int larger = Math.max(width, height);
        int sufficientWidth = (int)Math.pow(2, (int)Math.ceil(Math.log(larger)/Math.log(2)));
        double[][] temp = DiamondSquare.makeTable(3, 3, 3, 3, sufficientWidth + 1);
        DiamondSquare ds = new DiamondSquare(temp);
        ds.seed(System.currentTimeMillis());
        return convertToIntArray(ds.generate(new double[]{0, 0, sufficientWidth, 2, 0.4}), width, height);
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

    private static int[][] convertToIntArray(double[][] arr, int desiredWidth, int desiredHeight) {
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

}
