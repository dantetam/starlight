package io.github.dantetam.world;

import java.io.Serializable;
import java.util.Random;

import io.github.dantetam.person.BodyPart;
import io.github.dantetam.person.Injury;
import io.github.dantetam.person.Person;
import io.github.dantetam.xml.ConstructionTree;

/**
 * Created by Dante on 6/8/2017.
 */
public class CombatHandler implements Serializable {

    private Settlement settlement;
    private ConstructionTree tree;

    public CombatHandler(Settlement settlement, ConstructionTree tree) {
        this.settlement = settlement;
        this.tree = tree;
    }

    public void processMeleeAttack(Person attacker, Person target) {
        double rand = Math.random();
        //TODO Handle weapon is null, use fists
        Item weapon = attacker.weapon;
        if (weapon == null) {
            weapon = tree.copyItem("Fists", 1);
        }
        if (rand < weapon.getItemData("combatchance")) {
            float damage = 0;
            if (attacker.tile.neighbors(target.tile)) {
                damage = weapon.getItemData("combatmelee");
            }
            damage += new Random().nextGaussian() * (damage / 4.0f);
            if (damage > 0) {
                target.giveRandomInjury(new Injury("MeleeInjury", (int) damage, damage / 20.0f, true));
            }
        }
    }

    public void processRangedAttack(Person attacker, Person target) {
        double rand = Math.random();
        if (attacker.weapon == null) {
            return;
        }
        if (rand < attacker.weapon.getItemData("combatchance")) {
            float damage = 0;
            if (!attacker.tile.neighbors(target.tile) && attacker.weapon.hasItemData("combatshot")) {
                damage = attacker.weapon.getItemData("combatshot");
            }
            damage += new Random().nextGaussian() * (damage / 4.0f);
            if (damage > 0) {
                target.giveRandomInjury(new Injury("RangedInjury", (int) damage, damage / 20.0f, true));
            }
        }
    }

}
