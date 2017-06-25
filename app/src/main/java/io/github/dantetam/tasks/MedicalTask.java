package io.github.dantetam.tasks;

import io.github.dantetam.person.Person;
import io.github.dantetam.world.Building;

/**
 * Created by Dante on 6/5/2017.
 */
public class MedicalTask extends Task {

    private Person patient;

    public MedicalTask(int treatTime, Person patient) {
        super(treatTime);
        this.patient = patient;
    }

    @Override
    public void executeAction() {
       patient.treatRandomInjury();
    }

}
