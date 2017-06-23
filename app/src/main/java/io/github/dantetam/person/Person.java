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
    public Map<String, Integer> skills;
    private Map<String, Integer> skillPriorities;
    private Map<String, Integer> sortedSkillPrioritiesDes;
    public static int MAX_PRIORITY = 1, MIN_PRIORITY = 4, NO_PRIORITY = 5;
    transient public Body body;
    public int nutrition = 2;
    public static final int MAX_NUTRITION = 24;
    public int rest = 8;

    public Item weapon;
    public boolean isDrafted = false;

    public Faction faction;

    public Person(String name, List<String> possibleSkills, Faction faction) {
        this.name = name;
        this.items = new Inventory();
        queueTasks = new ArrayList<>();
        skills = new HashMap<>();
        skillPriorities = new HashMap<>();

        for (String skill: possibleSkills) {
            int initialLevel = (int) (Math.random() * 12);
            skills.put(skill, initialLevel);
            int initialPriority = (int) (Math.random() * 5) + 1;
            skillPriorities.put(skill, initialPriority);
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
        return body.root.getHealth() <= 0;
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

}
