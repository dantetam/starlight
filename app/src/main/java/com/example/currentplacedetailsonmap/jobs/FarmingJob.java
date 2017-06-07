package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.tasks.FarmingTask;
import com.example.currentplacedetailsonmap.tasks.MiningTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class FarmingJob extends Job {

    public Building farm;

    public FarmingJob(Settlement settlement, Building building) {
        super(settlement);
        this.farm = building;
    }

    @Override
    public String type() {
        return "Farming";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new FarmingTask((int) farm.getBuildingData("productionTimeForLump"), (int) farm.getBuildingData("lumpSize"), farm));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FarmingJob)) {
            return false;
        }
        FarmingJob miningJob = (FarmingJob) other;
        return this.farm.equals(miningJob.farm);
    }

    @Override
    public void cancelJob() {

    }
}
