package io.github.dantetam.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/8/2017.
 */
public class BodyPart implements Serializable {

    public String name, desc;
    private int health;
    public int maxHealth;
    public float proportionOfParent;

    public List<Injury> injuries;

    public BodyPart parent;
    public List<BodyPart> subBodyParts;

    public BodyPart(String name, String desc, float proportionOfParent, int maxHealth) {
        this.name = name;
        this.desc = desc;
        this.proportionOfParent = proportionOfParent;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        injuries = new ArrayList<>();
        subBodyParts = new ArrayList<>();
    }

    public int getHealth() {
        /*int total = maxHealth;
        for (Injury injury: injuries) {
            total -= injury.bodyPartDamage;
        }
        for (BodyPart subBodyPart: subBodyParts) {
            int subMaxHealth = subBodyPart.maxHealth;
            int subCurrentHealth = subBodyPart.getHealth();
            total -= (subMaxHealth - subCurrentHealth);
        }
        return Math.max(0, total);*/
        return Math.max(0, health);
    }

    public void setHealth(int newHealth) {
        int oldHealth = health;
        this.health = newHealth;
        if (parent != null) {
            int changeHealth = newHealth - oldHealth;
            parent.setHealth(parent.health + changeHealth);
        }
    }

    public void injure(Injury injury) {
        injuries.add(injury);
        setHealth(health - injury.bodyPartDamage);
    }

}
