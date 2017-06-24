package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.tasks.CraftingTask;
import io.github.dantetam.tasks.FarmingTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

/**
 * Created by Dante on 6/4/2017.
 */
public class CraftingJob extends Job {

    public Building farm;

    public CraftingJob(Settlement settlement, Building building) {
        super(settlement, building.getTile());
        this.farm = building;
    }

    @Override
    public String type() {
        return "Crafting";
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
        tasks.add(new CraftingTask((int) farm.getBuildingData("craftTime"), farm));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CraftingJob)) {
            return false;
        }
        CraftingJob miningJob = (CraftingJob) other;
        return this.farm.equals(miningJob.farm);
    }

    @Override
    public void cancelJob() {

    }
}
