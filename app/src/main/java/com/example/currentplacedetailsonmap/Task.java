package com.example.currentplacedetailsonmap;

import java.io.Serializable;

/**
 * Created by Dante on 6/3/2017.
 */
public abstract class Task implements Serializable {

    public int ticksLeft;

    public Task(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    public void tick() {
        if (ticksLeft == 0) {
            executeAction();
        }
        ticksLeft--;
    }

    public abstract void executeAction();

}
