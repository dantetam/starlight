package io.github.dantetam.tasks;

import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.jobs.FarmingJob;
import io.github.dantetam.jobs.Job;
import io.github.dantetam.jobs.MiningJob;
import io.github.dantetam.jobs.PlantCuttingJob;

import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 */
public class DeconstructionTask extends Task {

    public Settlement settlement;
    public Building building;
    public Tile tile;

    public DeconstructionTask(int ticksLeft, Settlement settlement, Building building, Tile tile) {
        super(ticksLeft);
        this.settlement = settlement;
        this.building = building;
        this.tile = tile;
    }

    @Override
    public void executeAction() {
        List<Job> jobs = settlement.availableJobsBySkill.get(building.getJobType());
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (building.getJobType().equals("Farming")) {
                if (((FarmingJob)job).farm.equals(building)) {
                    jobs.remove(i);
                    break;
                }
            }
            else if (building.getJobType().equals("Mining")) {
                if (((MiningJob)job).mine.equals(building)) {
                    jobs.remove(i);
                    break;
                }
            }
            else if (building.getJobType().equals("Plant Cutting")) {
                if (((PlantCuttingJob)job).lumberyard.equals(building)) {
                    jobs.remove(i);
                    break;
                }
            }
        }

        tile.removeBuilding();
    }

}
