package io.github.dantetam.tasks;

import io.github.dantetam.person.Person;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Inventory;
import io.github.dantetam.world.Settlement;

/**
 * Created by Dante on 6/5/2017.
 */
public class EatTask extends Task {

    private Settlement settlement;
    private Person person;
    private Building target;

    public EatTask(int eatTime, Settlement settlement, Person person, Building target) {
        super(eatTime);
        this.settlement = settlement;
        this.person = person;
        this.target = target;
    }

    @Override
    public void executeAction() {
        Inventory inventory = target.items;
        if (inventory.hasNutrition(Person.MAX_NUTRITION)) {
            Inventory eatItems = inventory.findNutrition(Person.MAX_NUTRITION);
            inventory.removeInventory(eatItems);
            person.nutrition = Person.MAX_NUTRITION; //TODO: Give the correct amount of food
        }
    }

}
