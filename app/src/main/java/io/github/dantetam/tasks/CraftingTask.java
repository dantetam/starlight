package io.github.dantetam.tasks;

import io.github.dantetam.world.Building;

/**
 * Created by Dante on 6/5/2017.
 */
public class CraftingTask extends Task {

    private Building mine;

    public CraftingTask(int mineTimeForLump, Building mine) {
        super(mineTimeForLump);
        this.mine = mine;
    }

    @Override
    public void executeAction() {
        mine.produce();
    }

}
