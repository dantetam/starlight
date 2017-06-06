package com.example.currentplacedetailsonmap.tasks;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;
import com.example.currentplacedetailsonmap.jobs.FarmingJob;
import com.example.currentplacedetailsonmap.jobs.MiningJob;
import com.example.currentplacedetailsonmap.jobs.PlantCuttingJob;

/**
 * Created by Dante on 6/4/2017.
 */
public class ConstructionTask extends Task {

    public Settlement settlement;
    public Building building;
    public Tile tile;

    public ConstructionTask(int ticksLeft, Settlement settlement, Building building, Tile tile) {
        super(ticksLeft);
        this.settlement = settlement;
        this.building = building;
        this.tile = tile;
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
        }
    }

}
