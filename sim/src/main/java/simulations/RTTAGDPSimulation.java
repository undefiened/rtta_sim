package simulations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import math.geom2d.Vector2D;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import other.*;
import simulations.world.Conflict;
import simulations.world.Drone;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.DoubleFunction;
import java.util.stream.Collectors;

/**
 * Created by undefiened on 11/21/17.
 */
public class RTTAGDPSimulation extends Simulation {
    final static long SEED = 1;

    static final long serialVersionUID = 1L;
    private final DoubleFunction<Double> probOfCancellation;
    private final double RTTA;
    private final double maxDelay;
    private final DoubleFunction<Double> longDelayCost;
    public int numberOfConflicts = 0;
    private final int n;

    private int maxTimeBeforeIntendedStart;

    public RTTAGDPSimulation(int n, double safetyZone, String region, double RTTA, int maxTimeBeforeIntendedStart,
                             double maxDelay, DoubleFunction<Double> longDelayCost, DoubleFunction<Double> probOfCancellation
                            ) throws Exception {
        super(n, safetyZone, region);
        this.n = n;
        this.maxTimeBeforeIntendedStart = maxTimeBeforeIntendedStart;
        this.probOfCancellation = probOfCancellation;

        this.RTTA = RTTA;
        this.maxDelay = maxDelay;
        this.longDelayCost = longDelayCost;

        loadDrones(n, region);


        resolveAllConflicts();
//        saveStatisticsToFile();
    }

    public JSONArray getSerializedResults(){
        JSONArray drones = new JSONArray();

        for (Drone drone: this.launchedDrones) {
            JSONObject serializedDrone = new JSONObject();
            serializedDrone.put("intent_arrival", drone.getIntentArrivalTime());
            serializedDrone.put("desired_start", drone.getOriginalStartTime());
            serializedDrone.put("actual_start", drone.startTime);
            serializedDrone.put("delay", drone.totalDelay);
            serializedDrone.put("was_cancelled", drone.isCancelsBeforeStart());
            serializedDrone.put("cancel_decision_time", drone.getCancelDecisionTime());
            serializedDrone.put("scheduling_time", drone.getRTTATime(this.RTTA));
            serializedDrone.put("cancelled_after_RTTA", drone.cancelsBeforeStart && drone.getRTTATime(this.RTTA) <= drone.getCancelDecisionTime());
            serializedDrone.put("ID", drone.getID());
            serializedDrone.put("type", drone.getType());

            drones.add(serializedDrone);
        }

        return drones;
    }

//    private void saveStatisticsToFile() throws IOException {
//        JSONArray delays = new JSONArray();
//        JSONObject sampleObject = new JSONObject();
//
//
//        System.out.println("Original start times");
//        System.out.println(this.launchedDrones.stream().mapToDouble(x -> x.getOriginalStartTime()).distinct().count());
//        System.out.println((long) this.launchedDrones.size());
//
//        for (Drone d :
//                this.launchedDrones.stream().sorted(Comparator.comparingDouble(Drone::getOriginalStartTime)).collect(Collectors.toList())) {
//            if (!d.cancelsBeforeStart) {
//                delays.add(d.totalDelay);
////                delays.add(d.startTime);
//            }
//        }
//
//        sampleObject.put("res", delays);
//
//        String folderName = "results";
//
//        if(!region.equals("nk")){
//            folderName = "results_bay";
//        }
//
//        String filename = String.format("%d_%.2f_%d.json", this.n, this.safetyZone, Math.round(this.RTTA));
//        Files.write(Paths.get(folderName + "/" + filename), sampleObject.toJSONString().getBytes());
//    }

    protected void loadDrones(int n, String region){
        dronesToLaunch = new PriorityQueue<>(new DroneComparatorEarliestRTTATime(this.RTTA));
//        dronesToLaunch = new PriorityQueue<>(new DroneComparatorEarliestStartTime());
//        dronesToLaunch = new PriorityQueue<>(new DroneComparatorIntentArrivalTime());
        allOriginalDrones = new ArrayList<>(n);

        launchedDrones = new TreeSet<>(new DroneComparatorEarliestEndTime());

        fillDrones(n, region);
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

    public void fillDrones(int n, String region){
        Random randIntentArrivalTime = new Random(SEED);
        Random randCancellation = new Random(SEED);
        Random randCancelDecisionTime = new Random(SEED);

        int max = this.maxTimeBeforeIntendedStart;
        int min = 0;

        try {
            FileReader dronesReader = DronesFiles.getDronesFile(region);
            JsonParser dronesJsonParser = new JsonParser();
            JsonArray dronesArray = (JsonArray) dronesJsonParser.parse(dronesReader);

            for (int i = 0; i < n; i++) {
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

                Random randType = new Random(droneJsonObj.toString().hashCode());
                Random randID = new Random(droneJsonObj.toString().hashCode());

                newDrone.setIntentArrivalTime(newDrone.startTime - randIntentArrivalTime.nextInt((max - min) + 1) + min);
                newDrone.setID(randID.nextLong(0, Long.MAX_VALUE));
                newDrone.setType(randType.nextInt(1, 10));

                double prob = probOfCancellation.apply( - (newDrone.getIntentArrivalTime() - newDrone.startTime));
                boolean cancel = randCancellation.nextDouble() < prob;

                if ( cancel ) {
                    newDrone.setCancelsBeforeStart(true);

                    double minCancel = newDrone.getIntentArrivalTime();
                    double maxCancel = newDrone.startTime;

                    newDrone.setCancelDecisionTime(randCancelDecisionTime.nextInt((int) ((maxCancel - minCancel) + 1)) + minCancel);
                }

                dronesToLaunch.add(newDrone);
                allOriginalDrones.add(newDrone);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

    protected boolean resolveFirstConflict(Drone nextDrone) throws Exception {
        boolean isAnyConflict = false;

        Drone droneToCompare = new Drone();
        droneToCompare.endTime = nextDrone.startTime;
        Conflict currentConflict;

        for (Drone drone:
                launchedDrones.tailSet(droneToCompare)){
            if (drone.cancelsBeforeStart && drone.cancelDecisionTime <= nextDrone.getRTTATime(this.RTTA)) {
                continue;
            }

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
