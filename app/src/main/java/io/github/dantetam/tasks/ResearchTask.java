package io.github.dantetam.tasks;

import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tech;
import io.github.dantetam.world.TechTree;

/**
 * Created by Dante on 6/5/2017.
 */
public class ResearchTask extends Task {

    private int lumpSize;
    private Settlement settlement;
    private TechTree techTree;
    public int researchSpeed;

    public ResearchTask(int mineTimeForLump, Settlement settlement, TechTree techTree, int researchSpeed) {
        super(mineTimeForLump);
        this.lumpSize = 1;
        this.settlement = settlement;
        this.techTree = techTree;
        this.researchSpeed = researchSpeed;
    }

    @Override
    public void executeAction() {
        techTree.research(researchSpeed);
        //techTree.forceUnlock(researchingTech);
    }

}
