package io.github.dantetam.person;

/**
 * Created by Dante on 6/9/2017.
 */
public class Injury {

    public BodyPart bodyPart;
    public String injuryType;
    public int bodyPartDamage;
    public float bloodLossPercentTick;
    public boolean fixable;
    public boolean treated;

    public Injury(BodyPart bodyPart, String injuryType, int bodyPartDamage, float bloodLossPercentTick, boolean fixable) {
        this.bodyPart = bodyPart;
        this.injuryType = injuryType;
        this.bodyPartDamage = bodyPartDamage;
        this.bloodLossPercentTick = bloodLossPercentTick;
        this.fixable = fixable;
        this.treated = true;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Injury)) {
            return false;
        }
        Injury injury = (Injury) other;
        return this.bodyPart.name.equals(injury.bodyPart.name) &&
                this.injuryType.equals(injury.injuryType) &&
                this.bodyPartDamage == injury.bodyPartDamage;
    }

}
