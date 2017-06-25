package io.github.dantetam.jobs;

import java.util.ArrayList;
import java.util.List;

import io.github.dantetam.person.Person;
import io.github.dantetam.tasks.MedicalTask;
import io.github.dantetam.tasks.MiningTask;
import io.github.dantetam.tasks.MoveTask;
import io.github.dantetam.tasks.Task;
import io.github.dantetam.world.Building;
import io.github.dantetam.world.Settlement;
import io.github.dantetam.world.Tile;

/**
 * Created by Dante on 6/4/2017.
 */
public class MedicalJob extends Job {

    public Person patient;

    public MedicalJob(Settlement settlement, Person patient) {
        super(settlement, patient.tile);
        this.patient = patient;
    }

    @Override
    public String type() {
        return "Medicine";
    }

    @Override
    public List<Task> createTasks() {
        if (!tile.neighbors(reservedPerson.tile)) {
            List<Task> tasks = new ArrayList<>();
            List<Tile> path = Settlement.pathfinder.findPath(reservedPerson.tile, tile);
            if (path != null) { //If a valid path was found
                for (Tile tile: path) {
                    Task localMoveTask = new MoveTask(reservedPerson.tileMoveSpeed(), reservedPerson, settlement, tile);
                    tasks.add(localMoveTask);
                }
            }
            return tasks;
        }

        //TODO: Base medical task time by severity of wound and skill of doctor

        List<Task> tasks = new ArrayList<>();
        tasks.add(new MedicalTask(15, patient));
        return tasks;
    }

    @Override
    public boolean doneCondition() {
        return patient.hasNoUntreatedInjuries();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MedicalJob)) {
            return false;
        }
        MedicalJob medicalJob = (MedicalJob) other;
        return this.patient.equals(medicalJob.patient);
    }

    @Override
    public void cancelJob() {

    }
}
