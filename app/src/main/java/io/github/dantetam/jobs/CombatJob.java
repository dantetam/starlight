package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.CombatTask;
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
public class CombatJob extends Job {

    public Person reservedPerson;
    public Person target;

    public CombatJob(Settlement settlement, Person person, Person target) {
        super(settlement, null);
        this.reservedPerson = person;
        this.target = target;
    }

    @Override
    public String type() {
        return "Combat";
    }

    @Override
    public List<Task> createTasks() {
        if (!target.tile.equals(reservedPerson.tile)) {
            List<Task> tasks = new ArrayList<>();
            List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, target.tile);
            if (path != null) { //If a valid path was found
                for (Tile tile: path) {
                    Task localMoveTask = new MoveTask(reservedPerson.tileMoveSpeed(), reservedPerson, settlement, tile);
                    tasks.add(localMoveTask);
                }
            }
            return tasks;
        }

        int time = 10;
        if (reservedPerson.weapon != null) {
            time = (int) reservedPerson.weapon.getItemData("combattime");
        }

        List<Task> tasks = new ArrayList<>();
        tasks.add(new CombatTask(time, settlement, reservedPerson, target));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, target.tile);
        if (path == null) { //If a valid path was found
            return true;
        }
        return target.isDead();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CombatJob)) {
            return false;
        }
        CombatJob combatJob = (CombatJob) other;
        return this.reservedPerson.equals(combatJob.reservedPerson) && this.target.equals(combatJob.target);
    }

    @Override
    public void cancelJob() {

    }

}
