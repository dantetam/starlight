package io.github.dantetam.tasks;

import io.github.dantetam.person.Person;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;

/**
 * Created by Dante on 6/5/2017.
 */
public class CombatMeleeTask extends Task {

    private Settlement settlement;
    private Person person, target;

    public CombatMeleeTask(int combatTime, Settlement settlement, Person person, Person target) {
        super(combatTime);
        this.settlement = settlement;
        this.person = person;
        this.target = target;
    }

    @Override
    public void executeAction() {
        settlement.combatHandler.processMeleeAttack(person, target);
    }

}
