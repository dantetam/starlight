package io.github.dantetam.world;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/15/2017.
 */
public class TechTree {

    public List<String> researchedBuildings;

    public TechTree() {
        researchedBuildings = new ArrayList<>();
    }

    public void researchTech(Tech tech) {
        for (String buildingString: tech.buildingsUnlocked) {
            researchedBuildings.add(buildingString);
        }
    }

}
