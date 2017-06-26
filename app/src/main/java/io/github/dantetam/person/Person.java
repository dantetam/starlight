package io.github.dantetam.person;

import io.github.dantetam.util.MapUtil;
import io.github.dantetam.world.Inventory;
import io.github.dantetam.world.Item;
import io.github.dantetam.world.Tile;
import io.github.dantetam.jobs.Job;
import io.github.dantetam.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dante on 6/1/2017.
 */
public class Person implements Serializable {

    public String name;
    public Inventory items;
    public Tile tile;
    public List<Task> queueTasks;
    public Job currentJob;
    public Map<String, Integer> skillLevels;
    public Map<String, Integer> skillExperiences;
    public static final int[] skillLevelsExpTotal = {0, 1000, 2100, 3300, 4600, 6000, 7500, 9100, 10800, 12600, 14500, 16500, 18600, 20800, 23100, 25500, 28000, 9999999};
    private Map<String, Integer> skillPriorities;
    private Map<String, Integer> sortedSkillPrioritiesDes;
    public static int MAX_PRIORITY = 1, MIN_PRIORITY = 4, NO_PRIORITY = 5;

    transient private Body body;

    public int nutrition = 2;
    public static final int MAX_NUTRITION = 24;
    public int rest = 8;
    public static final int MAX_REST = 16;
    private boolean asleep = false;

    public Item weapon;
    public boolean isDrafted = false;

    public Faction faction;

    public Person(String name, List<String> possibleSkills, Faction faction) {
        this.name = name;
        this.items = new Inventory();
        queueTasks = new ArrayList<>();
        skillLevels = new HashMap<>();
        skillPriorities = new HashMap<>();
        skillExperiences = new HashMap<>();

        for (String skill: possibleSkills) {
            if (skill == null) continue;

            int initialLevel = (int) (Math.random() * 15);
            skillLevels.put(skill, initialLevel);

            int initialPriority = 5 - (int) ((float) initialLevel / 15.0 * 5.0);
            initialPriority = Math.min(initialPriority, NO_PRIORITY);
            initialPriority = Math.max(initialPriority, MAX_PRIORITY);
            skillPriorities.put(skill, initialPriority);

            skillExperiences.put(skill, (skillLevelsExpTotal[initialLevel] + skillLevelsExpTotal[initialLevel + 1]) / 2);
        }
        sortSkillPriorities();

        this.faction = faction;
        faction.people.add(this);
    }

    public String getItemsString() {
        return items.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Person)) {
            return false;
        }
        Person person = (Person) other;
        return this.name.equals(person.name) && this.tile.equals(person.tile);
    }

    //The number of ticks passed when moving one tile in any direction
    public int tileMoveSpeed() {
        return 6;
    }

    public void initializeBody(Body body) {
        this.body = body;
    }

    public boolean isDead() {
        return body.getHealth() <= 0;
    }

    public void kill() {
        if (this.currentJob != null) {
            this.currentJob.reservedPerson = null;
            this.currentJob = null;
        }
    }

    //Sort the skills' priorities only when changed
    public Map<String, Integer> getSortedSkillPrioritiesDes() {
        return sortedSkillPrioritiesDes;
    }

    private void sortSkillPriorities() {
        sortedSkillPrioritiesDes = MapUtil.sortByValueDescending(skillPriorities);
    }

    public void changeSkillPriority(String skillName, int newValue) {
        skillPriorities.put(skillName, newValue);
        sortSkillPriorities();
    }

    public int[] reportExpForSkill(String skillName) {
        int skillLevel = skillLevels.get(skillName);
        int baseExp = skillLevelsExpTotal[skillLevel];
        int nextLevelExp = skillLevelsExpTotal[skillLevel + 1];
        int currentExp = skillExperiences.get(skillName);
        return new int[]{currentExp - baseExp, nextLevelExp - baseExp};
    }

    public void addExperience(String skillName, int addExp) {
        int skillLevel = skillLevels.get(skillName);
        //int baseExp = skillLevelsExpTotal[skillLevel];
        int nextLevelExp = skillLevelsExpTotal[skillLevel + 1];
        int currentExp = skillExperiences.get(skillName);
        skillExperiences.put(skillName, currentExp + addExp);

        if (currentExp + addExp >= nextLevelExp) {
            skillLevels.put(skillName, skillLevel + 1);
        }
    }

    public void removeExperience(String skillName, int addExp) {
        int skillLevel = skillLevels.get(skillName);
        int baseExp = skillLevelsExpTotal[skillLevel];
        int currentExp = skillExperiences.get(skillName);
        skillExperiences.put(skillName, currentExp - addExp);

        if (currentExp - addExp < baseExp) {
            skillLevels.put(skillName, skillLevel - 1);
        }
    }

    public void sleep() {
        asleep = true;
    }

    public void wakeUp() {
        asleep = false;
    }

    public boolean isAsleep() {
        return asleep;
    }

    public void giveRandomInjury(Injury injury) {
        body.giveRandomInjury(injury);
        if (body.getHealth() <= 0) {
            kill();
        }
    }

    public void treatRandomInjury() {

    }

    public int[] getNumInjuries() {return body.getNumInjuries();}

    public boolean hasNoUntreatedInjuries() {
        return body.hasNoUntreatedInjuries();
    }

    public void processBodyTick() {body.processBodyTick();}

    public int getHealth() {
        return body.getHealth();
    }

    public int getMaxHealth() {
        return body.maxHealth();
    }

}
