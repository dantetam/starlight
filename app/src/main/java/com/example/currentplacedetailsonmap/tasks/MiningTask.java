package com.example.currentplacedetailsonmap.tasks;

import com.example.currentplacedetailsonmap.Building;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.Tile;

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
