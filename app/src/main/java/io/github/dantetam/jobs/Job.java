package io.github.dantetam.jobs;

import io.github.dantetam.person.Person;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;
import io.github.dantetam.tasks.Task;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dante on 6/4/2017.
 *
 * A job can be taken by one person, it continually adds tasks (if the reserved person has none)
 * until the job satisfies the finishing condition.
 */
public abstract class Job implements Serializable {

    public Settlement settlement;
    public Tile tile;
    public Person reservedPerson;
    public abstract String type();

    public Job(Settlement settlement, Tile tile) {
        this.settlement = settlement;
        this.tile = tile;
        reservedPerson = null;
    }

    public abstract List<Task> createTasks();
    public abstract boolean doneCondition();
    public abstract boolean equals(Object other);
    public abstract void cancelJob();

}
