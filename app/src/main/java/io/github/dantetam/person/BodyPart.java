package io.github.dantetam.person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/8/2017.
 */
public class BodyPart {

    public String name, desc;
    public int maxHealth;
    public float proportionOfParent;

    public List<Injury> injuries;

    public List<BodyPart> subBodyParts;

    public BodyPart(String name, String desc, float proportionOfParent, int maxHealth) {
        this.name = name;
        this.desc = desc;
        this.proportionOfParent = proportionOfParent;
        this.maxHealth = maxHealth;
        injuries = new ArrayList<>();
        subBodyParts = new ArrayList<>();
    }

    public int getHealth() {
        int total = maxHealth;
        for (Injury injury: injuries) {
            total -= injury.bodyPartDamage;
        }
        for (BodyPart subBodyPart: subBodyParts) {
            int subMaxHealth = subBodyPart.maxHealth;
            int subCurrentHealth = subBodyPart.getHealth();
            total -= (subMaxHealth - subCurrentHealth);
        }
        return Math.max(0, total);
    }

    public class Injury {
        public String injuryType;
        public int bodyPartDamage;
        public float bloodLossPercentTick;
        public boolean fixable;

        public Injury(String injuryType, int bodyPartDamage, float bloodLossPercentTick, boolean fixable) {
            this.injuryType = injuryType;
            this.bodyPartDamage = bodyPartDamage;
            this.bloodLossPercentTick = bloodLossPercentTick;
            this.fixable = fixable;
        }
    }
}
