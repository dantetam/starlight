package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.tasks.FarmingTask;
import com.example.currentplacedetailsonmap.tasks.PlantCuttingTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class PlantCuttingJob extends Job {

    public Building lumberyard;

    public PlantCuttingJob(Settlement settlement, Building building) {
        super(settlement);
        this.lumberyard = building;
    }

    @Override
    public String type() {
        return "Plant Cutting";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new PlantCuttingTask((int) lumberyard.getBuildingData("productionTimeForLump"), (int) lumberyard.getBuildingData("lumpSize"), lumberyard));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PlantCuttingJob)) {
            return false;
        }
        PlantCuttingJob miningJob = (PlantCuttingJob) other;
        return this.lumberyard.equals(miningJob.lumberyard);
    }

    @Override
    public void cancelJob() {

    }
}
