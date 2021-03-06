package io.github.dantetam.jobs;

import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.tasks.FarmingTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class FarmingJob extends Job {

    public Building farm;

    public FarmingJob(Settlement settlement, Building building) {
        super(settlement, building.getTile());
        this.farm = building;
    }

    @Override
    public String type() {
        return "Farming";
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
        tasks.add(new FarmingTask((int) farm.getBuildingData("productionTimeForLump"), (int) farm.getBuildingData("lumpSize"), farm));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FarmingJob)) {
            return false;
        }
        FarmingJob miningJob = (FarmingJob) other;
        return this.farm.equals(miningJob.farm);
    }

    @Override
    public void cancelJob() {

    }
}
