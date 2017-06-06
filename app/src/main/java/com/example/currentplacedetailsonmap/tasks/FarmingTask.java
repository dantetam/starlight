package com.example.currentplacedetailsonmap.tasks;

import com.example.currentplacedetailsonmap.Building;

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
