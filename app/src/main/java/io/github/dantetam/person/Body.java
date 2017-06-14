package io.github.dantetam.person;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dante on 6/8/2017.
 */
public class Body implements Serializable {

    private Map<String, BodyPart> bodyPartsByName;
    public BodyPart root;

    public Body() {
        bodyPartsByName = new HashMap<>();
    }

    public Map<String, BodyPart> getBodyPartsByName() {
        return bodyPartsByName;
    }

    public BodyPart randomBodyPart() {
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

    public int getHealth() {
        return root.getHealth();
    }

    public int maxHealth() {
        return root.maxHealth;
    }

}
