package io.github.dantetam.tasks;

import io.github.dantetam.jobs.CookingJob;
import io.github.dantetam.jobs.FarmingJob;
import io.github.dantetam.jobs.MiningJob;
import io.github.dantetam.jobs.PlantCuttingJob;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

/**
 * Created by Dante on 6/4/2017.
 */
public class UpgradeTask extends Task {

    public Settlement settlement;
    public Building building;
    public Building upgradeBuilding;
    public Tile tile;

    public UpgradeTask(int ticksLeft, Settlement settlement, Building building, Building upgradeBuilding, Tile tile) {
        super(ticksLeft);
        this.settlement = settlement;
        this.building = building;
        this.upgradeBuilding = upgradeBuilding;
        this.tile = tile;
    }

    @Override
    public void executeAction() {
        settlement.upgradeBuilding(building, upgradeBuilding);
    }

}
