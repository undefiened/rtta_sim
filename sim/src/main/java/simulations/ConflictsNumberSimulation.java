package simulations;

import other.AllParams;
import simulations.world.Conflict;
import simulations.world.Drone;

import java.io.Serializable;
import java.util.ArrayList;

public class ConflictsNumberSimulation extends Simulation implements Serializable{
    static final long serialVersionUID = 10L;
    public int numberOfConflicts = 0;
    public ArrayList<Conflict> conflicts = new ArrayList<>();

    public ConflictsNumberSimulation(int n, double safetyZone, String region) throws Exception {
        super(n, safetyZone, region);
        loadDrones(n, region);
        System.out.println("Drones were loaded");

        int i = 0;
        while(!dronesToLaunch.isEmpty()){
            i += 1;
            Drone nextDrone = dronesToLaunch.poll();

            resolveAllConflicts(nextDrone);

            if(i % 1000 == 0){
                System.out.println(i);
            }
        }
    }

    public void resolveAllConflicts(Drone nextDrone) throws Exception {
        Drone droneToCompare = new Drone();
        droneToCompare.endTime = nextDrone.startTime;

        for (Drone drone: launchedDrones.tailSet(droneToCompare)){
            Conflict conflict = nextDrone.getConflictWith(drone);
            if (conflict.areInConflict){
                checkConflict(conflict);
                conflicts.add(conflict);
                numberOfConflicts += 1;
                conflict.mainDrone.setWasInConflict();
                conflict.anotherDrone.setWasInConflict();
            }
        }

        launchedDrones.add(nextDrone);
    }

    public void checkConflict(Conflict conflict) throws Exception {
        double dist = conflict.mainDrone.positionAt(conflict.cpaTime).minus(conflict.anotherDrone.positionAt(conflict.cpaTime)).norm();
        if(dist > conflict.mainDrone.safetyZoneRadius + conflict.anotherDrone.safetyZoneRadius){
            throw new Exception("There is some mistake with conflict detection!");
        }
    }

//    @Override
    public void resolveConflict(Conflict conflict) throws Exception {
//        numberOfConflicts += 1;
    }
}
