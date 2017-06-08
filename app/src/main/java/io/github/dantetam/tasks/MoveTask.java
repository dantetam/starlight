package io.github.dantetam.tasks;

import io.github.dantetam.person.Person;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

import java.io.Serializable;

/**
 * Created by Dante on 6/3/2017.
 */
public class MoveTask extends Task implements Serializable {

    public Person person;
    public Settlement settlement;
    public Tile dest;

    public MoveTask(int ticksLeft, Person person, Settlement settlement, Tile dest) {
        super(ticksLeft);
        this.person = person;
        this.settlement = settlement;
        this.dest = dest;
    }

    @Override
    public void executeAction() {
        settlement.movePerson(person, dest);
    }
}
