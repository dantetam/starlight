package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.CombatMeleeTask;
import io.github.dantetam.tasks.CombatRangedTask;
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
        List<Task> tasks = new ArrayList<>();

        int time = 10;
        if (reservedPerson.weapon != null) {
            time = (int) reservedPerson.weapon.getItemData("combattime");
        }

        if (reservedPerson.tile.trueManhattanDist(target.tile) == 1) {
            tasks.add(new CombatMeleeTask(time, settlement, reservedPerson, target));
            return tasks;
        }
        else if (reservedPerson.weapon.hasItemData("combatshot")) {
            float l2Dist = reservedPerson.tile.trueEuclideanDist(target.tile);
            if (l2Dist <= reservedPerson.weapon.getItemData("combatrange")) {
                tasks.add(new CombatRangedTask(time, settlement, reservedPerson, target));
                return tasks;
            }
        }

        List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, target.tile);
        if (path != null) { //If a valid path was found
            path.remove(path.size() - 1);
            for (Tile tile: path) {
                Task localMoveTask = new MoveTask(reservedPerson.tileMoveSpeed(), reservedPerson, settlement, tile);
                tasks.add(localMoveTask);
                if (reservedPerson.weapon.hasItemData("combatshot")) {
                    if (tile.trueEuclideanDist(target.tile) <= reservedPerson.weapon.getItemData("combatrange")) {
                        break;
                    }
                }
            }
        }
        return tasks;

        //TODO Check if the player is in range with either a melee or ranged weapon
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
