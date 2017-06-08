package io.github.dantetam.jobs;

import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.tasks.MiningTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class MiningJob extends Job {

    public Building mine;

    public MiningJob(Settlement settlement, Building building) {
        super(settlement, building.getTile());
        this.mine = building;
    }

    @Override
    public String type() {
        return "Mining";
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
        tasks.add(new MiningTask((int) mine.getBuildingData("productionTimeForLump"), (int) mine.getBuildingData("lumpSize"), mine));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MiningJob)) {
            return false;
        }
        MiningJob miningJob = (MiningJob) other;
        return this.mine.equals(miningJob.mine);
    }

    @Override
    public void cancelJob() {

    }
}
