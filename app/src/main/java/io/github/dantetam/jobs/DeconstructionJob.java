package io.github.dantetam.jobs;

import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.tasks.DeconstructionTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class DeconstructionJob extends Job {

    public Building building;

    public DeconstructionJob(Settlement settlement, Building building, Tile tile) {
        super(settlement, tile);
        this.building = building;
        this.tile = tile;
    }

    @Override
    public String type() {
        return "Construction";
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
        tasks.add(new DeconstructionTask(building.buildTime, settlement, building, tile));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return tile.getBuilding() == null;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DeconstructionJob)) {
            return false;
        }
        DeconstructionJob constructionJob = (DeconstructionJob) other;
        return this.building.equals(constructionJob.building) && this.tile.equals(constructionJob.tile);
    }

    @Override
    public void cancelJob() {

    }
}
