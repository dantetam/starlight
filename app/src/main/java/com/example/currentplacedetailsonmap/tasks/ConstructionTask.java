package com.example.currentplacedetailsonmap.tasks;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;

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
        tile.addBuilding(new Building(building));
    }

}
