/*
package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Inventory;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

*/
/**
 * Created by Dante on 6/4/2017.
 *//*

public class HaulJob extends Job {

    public Person person;
    public Building building;
    public Inventory subsetToCarry;
    public Tile dest;

    public HaulJob(Person person, Settlement settlement, Building building, Inventory subsetToCarry, Tile dest) {
        super(settlement, dest);
        this.person = person;
        this.building = building;
        this.subsetToCarry = subsetToCarry;
        this.dest = dest;
    }

    @Override
    public String type() {
        return "Hauling";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        List<Tile> path = Settlement.pathfinder.findPath(person.tile, dest);
        if (path != null) { //If a valid path was found
            for (Tile tile: path) {
                Task localMoveTask = new MoveTask(person.tileMoveSpeed(), person, settlement, tile);
                tasks.add(localMoveTask);
            }
        }
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        if (person.tile.equals(dest)) {
            return true;
        }
        List<Tile> path = Settlement.pathfinder.findPath(person.tile, dest);
        return path == null;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HaulJob)) {
            return false;
        }
        HaulJob miningJob = (HaulJob) other;
        return this.person.equals(miningJob.person) && this.dest.equals(miningJob.dest);
    }

    @Override
    public void cancelJob() {

    }
}
*/
