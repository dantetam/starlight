package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.ConstructionTree;
import com.example.currentplacedetailsonmap.Inventory;
import com.example.currentplacedetailsonmap.Item;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.tasks.ConstructionTask;
import com.example.currentplacedetailsonmap.tasks.CookingTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class CookingJob extends Job {

    public Building kitchen;
    public int goalNumMeals;

    public CookingJob(Settlement settlement, Building building, int goalNumMeals) {
        super(settlement);
        this.kitchen = building;
        this.goalNumMeals = goalNumMeals;
        //List<Item> possibleFood = kitchen.items.getItems();
    }

    @Override
    public String type() {
        return "Cooking";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new CookingTask((int) kitchen.getBuildingData("productionTimeForLump"), settlement, kitchen));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        int quantity = 0;
        for (Item item: kitchen.items.getItems()) {
            if (item.name.contains("Meal")) {
                quantity += item.quantity;
            }
        }
        return quantity >= goalNumMeals;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CookingJob)) {
            return false;
        }
        CookingJob constructionJob = (CookingJob) other;
        return this.kitchen.equals(constructionJob.kitchen) && this.goalNumMeals == goalNumMeals;
    }

    @Override
    public void cancelJob() {

    }

}
