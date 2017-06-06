package com.example.currentplacedetailsonmap.tasks;

import com.example.currentplacedetailsonmap.Building;

/**
 * Created by Dante on 6/5/2017.
 */
public class PlantCuttingTask extends Task {

    private int lumpSize;
    private Building lumberyard;

    public PlantCuttingTask(int farmTimeForLump, int lumpSize, Building farm) {
        super(farmTimeForLump);
        this.lumpSize = lumpSize;
        this.lumberyard = farm;
    }

    @Override
    public void executeAction() {
        lumberyard.produce();
    }

}
