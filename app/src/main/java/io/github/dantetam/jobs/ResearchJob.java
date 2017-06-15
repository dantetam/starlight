package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.tasks.CookingTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Item;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

/**
 * Created by Dante on 6/4/2017.
 */
public class ResearchJob extends Job {

    public Building lab;
    public TechTree techTree;
    public Tech researchingTech;

    public ResearchJob(Settlement settlement, Building building, TechTree techTree, Tech researchingTech) {
        super(settlement, building.getTile());
        this.lab = building;
        this.techTree = techTree;
        this.researchingTech = researchingTech;
        //List<Item> possibleFood = kitchen.items.getItems();
    }

    @Override
    public String type() {
        return "Researching";
    }

    @Override
    public List<Task> createTasks() {
        if (!tile.equals(reservedPerson.tile)) {
            List<Task> tasks = new ArrayList<>();
            List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, tile);
            if (path != null) { //If a valid path was found
                for (Tile tile: path) {
                    Task localMoveTask = new MoveTask(reservedPerson.tileMoveSpeed(), reservedPerson, settlement, tile);
                    tasks.add(localMoveTask);
                }
            }
            return tasks;
        }

        List<Task> tasks = new ArrayList<>();
        tasks.add(new ResearchTask((int) researchingTech.getResearchTime(), settlement, techTree, researchingTech));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return techTree.researched(researchingTech);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ResearchJob)) {
            return false;
        }
        ResearchJob researchJob = (ResearchJob) other;
        return this.lab.equals(researchJob.lab);
    }

    @Override
    public void cancelJob() {

    }

}
