package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.tasks.ConstructionTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.tasks.UpgradeTask;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Inventory;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

/**
 * Created by Dante on 6/4/2017.
 */
public class UpgradeJob extends Job {

    public Building building;
    public Building upgradeBuilding;
    public int recipeUsed;

    public UpgradeJob(Settlement settlement, Building building, Building upgradeBuilding, Tile tile, int recipeUsed) {
        super(settlement, tile);
        this.building = building;
        this.upgradeBuilding = upgradeBuilding;
        this.tile = tile;
        this.recipeUsed = recipeUsed;
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
                path.remove(0);
                for (Tile tile: path) {
                    Task localMoveTask = new MoveTask(reservedPerson.tileMoveSpeed(), reservedPerson, settlement, tile);
                    tasks.add(localMoveTask);
                }
            }
            else {
                System.err.println("Inaccessible");
            }
            return tasks;
        }

        List<Task> tasks = new ArrayList<>();
        tasks.add(new UpgradeTask(upgradeBuilding.buildTime, settlement, building, upgradeBuilding, tile));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return building.currentUpgrades.contains(upgradeBuilding.name);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UpgradeJob)) {
            return false;
        }
        UpgradeJob constructionJob = (UpgradeJob) other;
        return this.building.equals(constructionJob.building) &&
                this.tile.equals(constructionJob.tile) &&
                this.upgradeBuilding.equals(constructionJob.upgradeBuilding);
    }

    @Override
    public void cancelJob() {
        Inventory returnMaterials = upgradeBuilding.getCostRecipes().get(recipeUsed).input;
        settlement.nexus.items.addInventory(returnMaterials);
    }
}
