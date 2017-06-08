package io.github.dantetam.tasks;

import io.github.dantetam.world.Building;

/**
 * Created by Dante on 6/5/2017.
 */
public class MiningTask extends Task {

    private int lumpSize;
    private Building mine;

    public MiningTask(int mineTimeForLump, int lumpSize, Building mine) {
        super(mineTimeForLump);
        this.lumpSize = lumpSize;
        this.mine = mine;
    }

    @Override
    public void executeAction() {
        mine.produce();
    }

}
