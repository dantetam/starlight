package io.github.dantetam.tasks;

import io.github.dantetam.jobs.ResearchJob;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.TechTree;
import io.github.dantetam.world.Tile;
import io.github.dantetam.jobs.CookingJob;
import io.github.dantetam.jobs.FarmingJob;
import io.github.dantetam.jobs.MiningJob;
import io.github.dantetam.jobs.PlantCuttingJob;

/**
 * Created by Dante on 6/4/2017.
 */
public class ConstructionTask extends Task {

    private Settlement settlement;
    private Building building;
    private Tile tile;
    private TechTree techTree;

    public ConstructionTask(int ticksLeft, Settlement settlement, Building building, Tile tile, TechTree techTree) {
        super(ticksLeft);
        this.settlement = settlement;
        this.building = building;
        this.tile = tile;
        this.techTree = techTree;
    }

    @Override
    public void executeAction() {
        tile.addBuilding(building);

        if (building.getJobType() != null) {
            if (building.getJobType().equals("Farming")) {
                settlement.availableJobsBySkill.get(building.getJobType()).add(new FarmingJob(settlement, building));
            }
            else if (building.getJobType().equals("Mining")) {
                settlement.availableJobsBySkill.get(building.getJobType()).add(new MiningJob(settlement, building));
            }
            else if (building.getJobType().equals("Plant Cutting")) {
                settlement.availableJobsBySkill.get(building.getJobType()).add(new PlantCuttingJob(settlement, building));
            }
            else if (building.getJobType().equals("Cooking")) {
                settlement.availableJobsBySkill.get(building.getJobType()).add(new CookingJob(settlement, building, 30));
            }
            else if (building.getJobType().equals("Farming")) {
                settlement.availableJobsBySkill.get(building.getJobType()).add(new FarmingJob(settlement, building));
            }
            else if (building.getJobType().equals("Research")) {
                //TODO: Change speed of this, based on person's skill and body
                settlement.availableJobsBySkill.get(building.getJobType()).add(new ResearchJob(settlement, building, techTree, 1));
            }
        }
    }

}
