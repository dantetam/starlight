package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.CombatMeleeTask;
import io.github.dantetam.tasks.CombatRangedTask;
import io.github.dantetam.tasks.EatTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

/**
 * Created by Dante on 6/4/2017.
 */
public class EatJob extends Job {

    public Person reservedPerson;
    public Building target;

    public EatJob(Settlement settlement, Person person, Building target) {
        super(settlement, null);
        this.reservedPerson = person;
        this.target = target;
    }

    @Override
    public String type() {
        return "Essential";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();

        if (reservedPerson.tile.neighbors(target.getTile())) {
            tasks.add(new EatTask(15, settlement, reservedPerson, target));
            return tasks;
        }

        List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, target.getTile());
        if (path != null) { //If a valid path was found
            path.remove(path.size() - 1);
            for (Tile tile: path) {
                Task localMoveTask = new MoveTask(reservedPerson.tileMoveSpeed(), reservedPerson, settlement, tile);
                tasks.add(localMoveTask);
            }
        }
        return tasks;

        //TODO Check if the player is in range with either a melee or ranged weapon
    }

    @Override
    public boolean doneCondition() {
        List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, target.getTile());
        if (path == null) { //If a valid path was found
            return true;
        }
        return reservedPerson.nutrition >= 20;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof EatJob)) {
            return false;
        }
        EatJob combatJob = (EatJob) other;
        return this.reservedPerson.equals(combatJob.reservedPerson) && this.target.equals(combatJob.target);
    }

    @Override
    public void cancelJob() {

    }

}
