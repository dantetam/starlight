package io.github.dantetam.tasks;

import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;

/**
 * Created by Dante on 6/5/2017.
 */
public class CookingTask extends Task {

    private int lumpSize;
    private Building kitchen;

    public CookingTask(int mineTimeForLump, Settlement settlement, Building kitchen) {
        super(mineTimeForLump);
        this.lumpSize = 1;
        this.kitchen = kitchen;
    }

    @Override
    public void executeAction() {
        kitchen.produce();
    }

}
