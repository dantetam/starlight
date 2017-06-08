package io.github.dantetam.tasks;

import io.github.dantetam.world.Building;

/**
 * Created by Dante on 6/5/2017.
 */
public class FarmingTask extends Task {

    private int lumpSize;
    private Building farm;

    public FarmingTask(int farmTimeForLump, int lumpSize, Building farm) {
        super(farmTimeForLump);
        this.lumpSize = lumpSize;
        this.farm = farm;
    }

    @Override
    public void executeAction() {
        farm.produce();
    }

}
