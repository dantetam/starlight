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
    public static float MIN_SETTLEMENT_GEO_DIST = 500; //In meters

    public static Vector2f geoHome;

    public World() {
        settlements = new ArrayList<Settlement>();
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
                    for (Map.Entry<String, Integer> entry: sortedSkillsDescending.entrySet()) {
                        String skillName = entry.getKey();
                        if (settlement.availableJobsBySkill.get(skillName).size() > 0) {
                            Job assignedJob = settlement.availableJobsBySkill.get(skillName).get(0);
                            person.currentJob = assignedJob;
                            assignedJob.reservedPerson = person;

                            settlement.availableJobsBySkill.get(skillName).remove(0);
                        }
                    }
                }

                //The person may or may not have been assigned a job
                if (person.currentJob != null) {
                    if (person.currentJob.doneCondition()) {
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
            settlement.initializeSettlement(generateTiles(20, 20));
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

    public int[][] generateTiles(int width, int height) {
        int larger = Math.max(width, height);
        int sufficientWidth = (int)Math.pow(2, (int)Math.ceil(Math.log(larger)/Math.log(2)));
        double[][] temp = DiamondSquare.makeTable(3, 3, 3, 3, sufficientWidth + 1);
        DiamondSquare ds = new DiamondSquare(temp);
        ds.seed(System.currentTimeMillis());
        return convertToIntArray(ds.generate(new double[]{0, 0, sufficientWidth, 2, 0.4}), width, height);
    }

    private int[][] convertToIntArray(double[][] arr, int desiredWidth, int desiredHeight) {
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
