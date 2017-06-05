package com.example.currentplacedetailsonmap.jobs;

import com.example.currentplacedetailsonmap.Person;
import com.example.currentplacedetailsonmap.Settlement;
import com.example.currentplacedetailsonmap.tasks.Task;

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
    public Person reservedPerson;
    public abstract String type();

    public Job(Settlement settlement, Person reservedPerson) {
        this.settlement = settlement;
        this.reservedPerson = reservedPerson;
    }

    public abstract List<Task> createTasks();
    public abstract boolean doneCondition();

}
