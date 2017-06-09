package io.github.dantetam.world;

import java.util.Random;

import io.github.dantetam.person.BodyPart;
import io.github.dantetam.person.Injury;
import io.github.dantetam.person.Person;

/**
 * Created by Dante on 6/8/2017.
 */
public class CombatHandler {

    private Settlement settlement;

    public CombatHandler(Settlement settlement) {
        this.settlement = settlement;
    }

    public void processAttack(Person attacker, Person target) {
        double rand = Math.random();
        TODO Handle weapon is null, use fists
        if (rand < attacker.weapon.getItemData("combatchance")) {
            BodyPart randBodyPart = target.body.randomBodyPart();
            if (randBodyPart.getHealth() > 0) {
                float damage = 0;
                if (attacker.tile.trueManhattanDist(target.tile) == 1) {
                    damage = attacker.weapon.getItemData("combatmelee");
                } else {
                    if (attacker.weapon.hasItemData("combatshot")) {
                        damage = attacker.weapon.getItemData("combatshot");
                    }
                }
                damage += new Random().nextGaussian() * (damage / 4.0f);
                if (damage > 0) {
                    randBodyPart.injuries.add(new Injury("Injury", (int) damage, damage / 20.0f, true));
                }
            }
        }
    }

}
