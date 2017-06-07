package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.ConstructionTree;
import com.example.currentplacedetailsonmap.Inventory;
import com.example.currentplacedetailsonmap.Item;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.tasks.ConstructionTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class CookingJob extends Job {

    public Building kitchen;
    public int goalFood;

    public CookingJob(Settlement settlement, Building building, ConstructionTree tree, int goalFood) {
        super(settlement);
        this.kitchen = building;
        List<Item> possibleFood = kitchen.items.getItems();
        for (Item item: possibleFood) {
            if (tree.itemIsInGroup("RawFood", item.name)) {
                //TODO:
            }
        }
    }

    @Override
    public String type() {
        return "Cooking";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new CookingTask(kitchen.getBuildingData("productionTimeForLump"), settlement, kitchen));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return kitchen
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CookingJob)) {
            return false;
        }
        CookingJob constructionJob = (CookingJob) other;
        return this.building.equals(constructionJob.building) && this.tile.equals(constructionJob.tile);
    }

    @Override
    public void cancelJob() {
        Inventory returnMaterials = building.getCostRecipes().get(recipeUsed).input;
        settlement.nexus.items.addInventory(returnMaterials);
    }
}
