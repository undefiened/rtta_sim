package simulations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import math.geom2d.Vector2D;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import other.DroneComparatorEarliestEndTime;
import other.DroneComparatorEarliestRTTATime;
import other.DroneComparatorEarliestStartTime;
import other.DronesFiles;
import simulations.world.Conflict;
import simulations.world.Drone;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.DoubleFunction;
import java.util.stream.Collectors;

public class ConflictProbabilitySimulation extends Simulation {
    final static long SEED = 1;

    static final long serialVersionUID = 1L;
    public int numberOfConflicts = 0;
    public int numberOfNewConflicts = 0;
    private final int numberToLaunch;
    private final int numberOfDronesToTest;
    public ArrayList<Drone> dronesToTest;

    public ConflictProbabilitySimulation(int numberToLaunch, int numberOfDronesToTest, double safetyZone, String region)
            throws Exception {
        super(numberToLaunch, safetyZone, region);
        this.numberToLaunch = numberToLaunch;
        this.numberOfDronesToTest = numberOfDronesToTest;

        loadDrones(numberToLaunch, numberOfDronesToTest, region);


        resolveAllConflicts();
        countNewConflicts();
//        saveStatisticsToFile();
    }

    private void saveStatisticsToFile() throws IOException {
        JSONArray delays = new JSONArray();
        JSONObject sampleObject = new JSONObject();


        sampleObject.put("numberOfNewConflicts", numberOfNewConflicts);

        String folderName = "results";

        if(!region.equals("nk")){
            folderName = "results_bay";
        }

        String filename = String.format("%d_%d_%.2f.json", this.numberToLaunch, this.numberOfDronesToTest, this.safetyZone);
        Files.write(Paths.get(folderName + "/" + filename), sampleObject.toJSONString().getBytes());
    }

    protected void loadDrones(int n, int numberOfDronesToTest, String region){
//        dronesToLaunch = new PriorityQueue<>(new DroneComparatorEarliestRTTATime(this.RTTA));
        dronesToLaunch = new PriorityQueue<>(new DroneComparatorEarliestStartTime());
        dronesToTest = new ArrayList<>();
//        dronesToLaunch = new PriorityQueue<>(new DroneComparatorIntentArrivalTime());
        allOriginalDrones = new ArrayList<>(n);

        launchedDrones = new TreeSet<>(new DroneComparatorEarliestEndTime());

        fillDrones(n, numberOfDronesToTest, region);
    }

    public boolean isStartTimeAlreadyPresent(double startTime){
        for (Drone d :
                dronesToLaunch) {
            if (d.getOriginalStartTime() == startTime) {
                return true;
            }
        }
        return false;
    }

    public void fillDrones(int n, int nToTest, String region){
        try {
            FileReader dronesReader = DronesFiles.getDronesFile(region);
            JsonParser dronesJsonParser = new JsonParser();
            JsonArray dronesArray = (JsonArray) dronesJsonParser.parse(dronesReader);

            for (int i = 0; i < n; i++) {
                Drone newDrone = loadDroneByID(dronesArray, i);
                dronesToLaunch.add(newDrone);
                allOriginalDrones.add(newDrone);
            }

            for (int i = n+1; i < n + nToTest; i++) {
                Drone newDrone = loadDroneByID(dronesArray, i);
                dronesToTest.add(newDrone);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    protected Drone loadDroneByID(JsonArray dronesArray, int i){
        JsonObject droneJsonObj = (JsonObject)dronesArray.get(i);
        JsonArray start = (JsonArray)droneJsonObj.get("start");
        JsonArray end = (JsonArray)droneJsonObj.get("end");
        double startTime = droneJsonObj.get("start_time").getAsInt();

        while (isStartTimeAlreadyPresent(startTime)) {
            startTime = startTime + 0.01;
        }

        Drone newDrone = new Drone(
                new Vector2D(start.get(1).getAsInt(), start.get(0).getAsInt()),
                new Vector2D(end.get(1).getAsInt(), end.get(0).getAsInt()),
                startTime,
                safetyZone,
                params.speedPxS
        );

        return newDrone;
    }

    protected void resolveAllConflicts() throws Exception {
        while(!dronesToLaunch.isEmpty()){
            Drone nextDrone = dronesToLaunch.poll();
//            System.out.format("Start time: %.1f; Intent arrival: %.1f; RTTA time: %.1f %n", nextDrone.startTime, nextDrone.intentArrivalTime, nextDrone.getRTTATime(this.RTTA));

            boolean isAnyConflict = resolveFirstConflict(nextDrone);

            while(isAnyConflict){
                isAnyConflict = resolveFirstConflict(nextDrone);
            }

            launchedDrones.add(nextDrone);
        }

        testResults();
    }

    protected void countNewConflicts() throws Exception {
        for (Drone drone: dronesToTest) {
            boolean anyConflict = false;
            for (Drone launchedDrone: launchedDrones) {
                currentConflict = drone.getConflictWith(launchedDrone);
                if (currentConflict.areInConflict) {
                    anyConflict = true;
                    break;
                }
            }

            if(anyConflict) {
                numberOfNewConflicts += 1;
            }
        }
    }

    protected boolean resolveFirstConflict(Drone nextDrone) throws Exception {
        boolean isAnyConflict = false;

        Drone droneToCompare = new Drone();
        droneToCompare.endTime = nextDrone.startTime;
        Conflict currentConflict;

        for (Drone drone:
                launchedDrones.tailSet(droneToCompare)){
            currentConflict = nextDrone.getConflictWith(drone);
            if (currentConflict.areInConflict){
                isAnyConflict = true;
                resolveConflict(currentConflict);
                numberOfConflicts += 1;
                break;
            }
        }

        return isAnyConflict;
    }

    public void resolveConflict(Conflict conflict) throws Exception {
        double expectedDelay = conflict.getNecessaryDelayTimeForFirstDrone();
        conflict.mainDrone.delay(expectedDelay);

        if(conflict.mainDrone.resolvedDrones.contains(conflict.anotherDrone)){
            throw new Exception("The conflict should be already resolved");
        }

        conflict.mainDrone.resolvedDrones.add(conflict.anotherDrone);
//        testExpectedDelay(conflict, expectedDelay);
    }

    public void testResults() throws Exception {
//        System.out.println("Final size " + launchedDrones.size());
//        System.out.println("Sum of time without delays " + launchedDrones.stream().mapToDouble(drone -> drone.endTime - drone.startTime).sum());
//        System.out.println("Sum of time with delays " + launchedDrones.stream().mapToDouble(drone -> drone.endTime - drone.startTime + drone.totalDelay).sum());


        Object[] drones = launchedDrones.toArray();
        for (int i = 1; i < launchedDrones.size(); i++) {
            for (int j = 0; j < i; j++) {
                Drone d1 = (Drone)drones[i];
                Drone d2 = (Drone)drones[j];

                Conflict conflict = d1.getConflictWith(d2);

                if(conflict.areInConflict && !d1.cancelsBeforeStart && !d2.cancelsBeforeStart){
                    throw new Exception("Two drones should not be in conflict as a result!");
                }
            }
        }

        for (Drone drone:
                launchedDrones) {
            if(drone.wasDelayed()){
                drone.delay(-1);
                if(!isAnyConflict(drone)){
//                    throw new Exception("Without a delay there should be a conflict!");
                    System.out.println("WWWW");
                }
                drone.delay(1);
            }
        }
    }

    public boolean isAnyConflict(Drone drone) throws Exception {
        boolean isAnyConflict = false;
        for (Drone anotherDrone: drone.resolvedDrones) {
            Conflict conflict = drone.getConflictWith(anotherDrone);
            if(conflict.areInConflict){
                isAnyConflict = true;
            }
        }

        return isAnyConflict;
    }

    public void testExpectedDelay(Conflict conflict, double delay) throws Exception {
        Conflict newConflict = conflict.mainDrone.getConflictWith(conflict.anotherDrone);
        if(newConflict.areInConflict){
            throw new Exception("The conflict was not resolved");
        }

        if(newConflict.cpaDistance >= newConflict.getConflictDistance()*1.25){
            throw new Exception("Delay time was calculated incorrectly!");
        }
    }
}
