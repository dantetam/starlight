package com.example.currentplacedetailsonmap.tasks;

import java.io.Serializable;

/**
 * Created by Dante on 6/3/2017.
 *
 * A task is simply a directive to do something,
 * it takes some number of ticks to complete a task,
 * which then initiates an action.
 */
public abstract class Task implements Serializable {

    public int ticksLeft, originalTicksLeft;

    public Task(int ticksLeft) {
        this.ticksLeft = ticksLeft;
        this.originalTicksLeft = ticksLeft;
    }

    public void tick() {
        if (ticksLeft == 0) {
            executeAction();
        }
        ticksLeft--;
    }

    public abstract void executeAction();

}
