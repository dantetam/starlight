package io.github.dantetam.person;

import io.github.dantetam.world.Inventory;
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
    public Map<String, Integer> skillPriorities;
    public static int MAX_PRIORITY = 1, MIN_PRIORITY = 4, NO_PRIORITY = 5;
    public Body body;

    public Person(String name, List<String> possibleSkills) {
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

}
