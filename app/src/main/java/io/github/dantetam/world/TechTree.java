package io.github.dantetam.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dantetam.person.Faction;

/**
 * Created by Dante on 7/4/2016.
 */
public class TechTree {

    private Faction faction;
    private Settlement settlement;
    private Tech root;
    public void setRoot(Tech root) {this.root = root;}

    public HashMap<String, Tech> techMap;
    public HashMap<Tech, Boolean> researchedTech;

    //public static HashMap<String, Tech> cityStateTechMap;
    public static HashMap<Tech, Boolean> cityStateTech;

    public List<Tech> researchingTechQueue;

    //public HashMap<BuildingType, List<BuildingType>> allowedModules;
    public HashMap<String, Boolean> allowedBuildings;

    //These values are used for interfaces that render the tech tree GUI
    //presumably as a GridLayout.
    public float screenCenterX, screenCenterY;
    public int sightX = 2, sightY = 3;
    public int minX, maxX, minY, maxY;

    public TechTree(Faction faction, Settlement settlement) {
        this.faction = faction;
        this.settlement = settlement;

        techMap = new HashMap<>();
        researchedTech = new HashMap<>();

        //cityStateTechMap = new HashMap<>();
        cityStateTech = new HashMap<>();

        researchingTechQueue = new ArrayList<>();

        allowedBuildings = new HashMap<>();
    }

    //Put a specified amount of research into the tree. Return any extra left.
    public int research(int inputScience) {
        if (researchingTechQueue.size() == 0) {
            throw new RuntimeException(faction.name + " is not researching a technology.");
            //return;
        }
        Tech researching = researchingTechQueue.get(0);
        researching.research(inputScience);
        if (researching.researched()) {
            activateTechAbilities(researching);
            researchingTechQueue.remove(0);
            return researching.researchCompleted - researching.researchNeeded;
        }
        return 0;
    }

    public void forceUnlock(Tech tech) {
        tech.forceUnlock();
        activateTechAbilities(tech);
    }

    public void activateTechAbilities(Tech tech) {
        researchedTech.put(tech, true);

        for (String buildingString: tech.unlockedBuildings) {
            this.allowedBuildings.put(buildingString, true);
        }
    }

    /*
    Recursive beeline of a tech through its 'natural' prereqs (parent within the tree structure),
    as well as its 'artificial' prereqs (extra prereqs added on and defined, since it makes
    no sense for a node in a tree to have two direct parents).

    Add all necessary techs as well as the first tech argument called to the research queue.
    Topological sort?
     */
    public void beeline(Tech tech) {
        if (researchingTechQueue.contains(tech)) {
            return;
        }
        if (tech.researched()) {
            return;
        }
        if (tech.researchable()) {

        }
        else {
            beeline(tech.parent);
            for (Tech extra: tech.extraReqs) {
                beeline(extra);
            }
        }
        if (!researchingTechQueue.contains(tech))
            researchingTechQueue.add(tech);
    }

    /*public List<Tech> traverse(Condition cond) {
        return traverse(root, cond);
    }
    private List<Tech> traverse(Tech techInspect, Condition cond) {
        List<Tech> techFulfillingCond = new ArrayList<>();
        if (cond.allowed(techInspect)) {
            techFulfillingCond.add(techInspect);
            int children = techInspect.unlockedTechs.size();
            if (children > 0) {
                for (int i = 0; i < children; i++) {
                    Tech techChildInspect = techInspect.unlockedTechs.get(i);
                    List<Tech> techChildFulfillingCond = traverse(techChildInspect, cond);
                    for (Tech t: techChildFulfillingCond) {
                        techFulfillingCond.add(t);
                    }
                }
            }
        }
        return techFulfillingCond;
    }

    public List<Tech> findBorderTech() {
        Condition borderCondition = new Condition() {
            //public String match = null;
            public boolean allowed(Object obj) {
                if (!(obj instanceof Tech)) return false; //Safety check
                Tech tech = (Tech) obj;
                return tech.researched() && tech.hasUnresearchedChildren();
            }
        };
        return traverse(borderCondition);
    }

    public List<Tech> getResearchableTech() {
        List<Tech> borderTech = findBorderTech();
        List<Tech> researchable = new ArrayList<>();
        for (Tech t: borderTech) {
            for (Tech child: t.unlockedTechs) {
                if (!child.researched()) {
                    researchable.add(child);
                }
            }
        }
        return researchable;
    }

    public void unlock(String techNameToUnlock) {
        Tech techToUnlock = techMap.get(techNameToUnlock);
        forceUnlock(techToUnlock);
    }

    public void traverseAndPrint() {
        traverseAndPrint(root, 0);
    }
    private void traverseAndPrint(Tech t, int level) {
        String stringy = "";
        for (int i = 0; i < level; i++) {
            stringy += ".   .";
        }
        System.out.println(stringy + t.name);
        for (Tech tech: t.unlockedTechs) {
            traverseAndPrint(tech, level + 1);
        }
    }

    public void modifyX(float dx) {
        screenCenterX += dx;
        if (screenCenterX < minX) screenCenterX = minX;
        if (screenCenterX > maxX - 3) screenCenterX = maxX - 3;
    }

    public void modifyY(float dy) {
        screenCenterY += dy;
        if (screenCenterY < minY) screenCenterY = minY;
        if (screenCenterY > maxY - 3) screenCenterY = maxY - 3;
    }*/

}
