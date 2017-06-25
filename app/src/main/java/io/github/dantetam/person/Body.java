package io.github.dantetam.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dante on 6/8/2017.
 */
public class Body implements Serializable {

    private Map<String, BodyPart> bodyPartsByName;
    private BodyPart root;
    private List<Injury> currentUntreatedInjuries;

    public Body() {
        bodyPartsByName = new HashMap<>();
        currentUntreatedInjuries = new ArrayList<>();
    }

    public Map<String, BodyPart> getBodyPartsByName() {
        return bodyPartsByName;
    }

    private BodyPart randomBodyPart() {
        return randomBodyPart(root);
    }
    private BodyPart randomBodyPart(BodyPart current) {
        double rand = Math.random();
        double runSum = 0;
        //Choose one of the subparts if available, may have nested subparts
        for (BodyPart child: current.subBodyParts) {
            if (rand > runSum && rand <= runSum + child.proportionOfParent) {
                return randomBodyPart(child);
            }
            else {
                runSum += child.proportionOfParent;
            }
        }
        //Return this current part if one of the subparts was not hit or there are no subparts
        return current;
    }

    public void giveRandomInjury(Injury injury) {
        BodyPart randBodyPart = this.randomBodyPart();
        if (randBodyPart.getHealth() > 0) {
            randBodyPart.injure(injury);
            currentUntreatedInjuries.add(injury);
        }
    }

    public void treatRandomInjury() {
        if (hasNoUntreatedInjuries()) {
            return;
        }
        else {
            int index = (int) (Math.random() * currentUntreatedInjuries.size());
            currentUntreatedInjuries.get(index).treated = true;
            currentUntreatedInjuries.remove(index);
        }
    }

    public boolean hasNoUntreatedInjuries() {
        return currentUntreatedInjuries.size() == 0;
    }

    public int getHealth() {
        return root.getHealth();
    }

    public int maxHealth() {
        return root.maxHealth;
    }

    public void setRoot(BodyPart part) {
        this.root = part;
    }

}
