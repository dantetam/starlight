package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Person;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.tasks.ConstructionTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Dante on 6/4/2017.
 */
public class ConstructionJob extends Job {

    public Building building;
    public Tile tile;

    public ConstructionJob(Settlement settlement, Building building, Tile tile) {
        super(settlement);
        this.building = building;
        this.tile = tile;
    }

    @Override
    public String type() {
        return "Construction";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new ConstructionTask(building.buildTime, settlement, building, tile));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return tile.getBuilding().equals(building);
    }
}
