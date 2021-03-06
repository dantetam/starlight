package io.github.dantetam.jobs;

import io.github.dantetam.person.Person;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class MoveJob extends Job {

    public Person person;
    public int tileMoveSpeed;
    public Tile dest;

    public MoveJob(Person person, int tileMoveSpeed, Settlement settlement, Tile dest) {
        super(settlement, dest);
        this.person = person;
        this.tileMoveSpeed = tileMoveSpeed;
        this.dest = dest;
    }

    @Override
    public String type() {
        return "Moving";
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
        if (!(other instanceof MoveJob)) {
            return false;
        }
        MoveJob miningJob = (MoveJob) other;
        return this.person.equals(miningJob.person) && this.dest.equals(miningJob.dest);
    }

    @Override
    public void cancelJob() {

    }
}
