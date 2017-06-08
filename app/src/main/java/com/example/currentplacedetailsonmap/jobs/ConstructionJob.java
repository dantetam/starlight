package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Inventory;
import com.example.currentplacedetailsonmap.Person;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.tasks.ConstructionTask;
import com.example.currentplacedetailsonmap.tasks.MoveTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Dante on 6/4/2017.
 */
public class ConstructionJob extends Job {

    public Building building;
    public int recipeUsed;

    public ConstructionJob(Settlement settlement, Building building, Tile tile, int recipeUsed) {
        super(settlement, tile);
        this.building = building;
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
        tasks.add(new ConstructionTask(building.buildTime, settlement, building, tile));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        if (tile.getBuilding() == null) {
            return false;
        }
        return tile.getBuilding().equals(building);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ConstructionJob)) {
            return false;
        }
        ConstructionJob constructionJob = (ConstructionJob) other;
        return this.building.equals(constructionJob.building) && this.tile.equals(constructionJob.tile);
    }

    @Override
    public void cancelJob() {
        Inventory returnMaterials = building.getCostRecipes().get(recipeUsed).input;
        settlement.nexus.items.addInventory(returnMaterials);
    }
}
