package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.tasks.ConstructionTask;
import com.example.currentplacedetailsonmap.tasks.MiningTask;
import com.example.currentplacedetailsonmap.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class MiningJob extends Job {

    public Building mine;

    public MiningJob(Settlement settlement, Building building) {
        super(settlement);
        this.mine = building;
    }

    @Override
    public String type() {
        return "Mining";
    }

    @Override
    public List<Task> createTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new MiningTask((int) mine.getBuildingData("productionTimeForLump"), (int) mine.getBuildingData("lumpSize"), mine));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MiningJob)) {
            return false;
        }
        MiningJob miningJob = (MiningJob) other;
        return this.mine.equals(miningJob.mine);
    }

    @Override
    public void cancelJob() {

    }
}
