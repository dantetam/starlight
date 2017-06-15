package io.github.dantetam.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dante on 7/4/2016.
 */
public class Tech {

    public String name;
    public Tech parent;
    public List<Tech> extraReqs;
    public List<Tech> unlockedTechs;

    public List<String> unlockedBuildings;

    public int researchCompleted, researchNeeded;

    public String iconName;
    public int treeOffsetX, treeOffsetY;

    public Tech(String n, int researchCompleted, int researchNeeded) {
        name = n;

        this.researchCompleted = researchCompleted;
        this.researchNeeded = researchNeeded;

        extraReqs = new ArrayList<>();
        unlockedTechs = new ArrayList<>();
        unlockedBuildings = new ArrayList<>();
    }

    public boolean researched() {
        return researchCompleted >= researchNeeded;
    }

    public boolean researchable() {
        for (Tech req: extraReqs) {
            if (!req.researched()) {
                return false;
            }
        }
        if (parent != null) {
            return parent.researched();
        }
        return !researched();
    }

    public void research(int researchAmount) {
        researchCompleted += researchAmount;
    }

    public void forceUnlock() {
        researchCompleted = researchNeeded;
    }

    public boolean hasUnresearchedChildren() {
        for (Tech tech: unlockedTechs) {
            if (!tech.researched()) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Tech)) {
            return false;
        }
        Tech tech = (Tech) other;
        return name.equals(tech.name);
    }

    public String toString() {
        return name;
    }

}
