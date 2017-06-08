package io.github.dantetam.person;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dante on 6/8/2017.
 */
public class Body {

    private Map<String, BodyPart> bodyPartsByName;
    public BodyPart root;

    public Body() {
        bodyPartsByName = new HashMap<>();
    }

}
