package io.github.dantetam.world;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/15/2017.
 */
public class Tech {

    public String name;
    public String desc;

    public List<String> buildingsUnlocked;
    public int researchCompleted, researchNeededTotal;
    public List<Tech> futureTech;

    public Tech(String name, String desc) {
        this.name = name;
        this.desc = desc;
        buildingsUnlocked = new ArrayList<>();
        futureTech = new ArrayList<>();
    }

    public boolean isCompleted() {
        return researchCompleted >= researchNeededTotal;
    }

    public void addResearch(int research) {
        researchCompleted += research;
        researchCompleted = Math.max(0, Math.min(researchNeededTotal, researchCompleted));
    }

}
