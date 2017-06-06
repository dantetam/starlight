package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.tasks.ConstructionTask;
import com.example.currentplacedetailsonmap.tasks.DeconstructionTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class DeconstructionJob extends Job {

    public Building building;
    public Tile tile;

    public DeconstructionJob(Settlement settlement, Building building, Tile tile) {
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
        tasks.add(new DeconstructionTask(building.buildTime, settlement, building, tile));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return tile.getBuilding() == null;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DeconstructionJob)) {
            return false;
        }
        DeconstructionJob constructionJob = (DeconstructionJob) other;
        return this.building.equals(constructionJob.building) && this.tile.equals(constructionJob.tile);
    }
}
