package io.github.dantetam.person;

/**
 * Created by Dante on 6/9/2017.
 */
public class Injury {
    public String injuryType;
    public int bodyPartDamage;
    public float bloodLossPercentTick;
    public boolean fixable;
    public boolean treated;

    public Injury(String injuryType, int bodyPartDamage, float bloodLossPercentTick, boolean fixable) {
        this.injuryType = injuryType;
        this.bodyPartDamage = bodyPartDamage;
        this.bloodLossPercentTick = bloodLossPercentTick;
        this.fixable = fixable;
        this.treated = true;
    }
}
