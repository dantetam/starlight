package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.tasks.FarmingTask;
import com.example.currentplacedetailsonmap.tasks.MoveTask;
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
        super(settlement, building.getTile());
        this.lumberyard = building;
    }

    @Override
    public String type() {
        return "Plant Cutting";
    }

    @Override
    public List<Task> createTasks() {
        if (!tile.equals(reservedPerson.tile)) {
            List<Task> tasks = new ArrayList<>();
            List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, tile);
            if (path != null) { //If a valid path was found
                for (Tile tile: path) {
                    Task localMoveTask = new MoveTask(reservedPerson.tileMoveSpeed(), reservedPerson, settlement, tile);
                    tasks.add(localMoveTask);
                }
            }
            return tasks;
        }

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
