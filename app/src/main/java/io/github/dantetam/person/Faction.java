package io.github.dantetam.person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/9/2017.
 */
public class Faction {

    public String name;
    public List<Person> people;

    public Faction(String name) {
        this.name = name;
        people = new ArrayList<>();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Faction)) {
            return false;
        }
        return name.equals(((Faction) other).name);
    }

}
