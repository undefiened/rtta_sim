package simulations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import math.geom2d.Vector2D;
import other.*;
import simulations.world.Conflict;
import simulations.world.Drone;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.*;

/**
 * Created by undefiened on 11/21/17.
 */
public abstract class Simulation implements Serializable {
    static final long serialVersionUID = 10L;
    public PriorityQueue<Drone> dronesToLaunch;
    public TreeSet<Drone> launchedDrones;
    public ArrayList<Drone> allOriginalDrones;
    public String region;
    public Conflict currentConflict;
    public double safetyZone;

    public Params params;

    public Simulation(int n, double safetyZone, String region) throws Exception {
        this.region = region;
        AllParams.getInstance().setRegion(region);
        params = AllParams.get();
        this.safetyZone = params.convertMToPx(safetyZone);
//        loadDrones(n, region);
        currentConflict = new Conflict();
    }

    protected void loadDrones(int n, String region){
        dronesToLaunch = new PriorityQueue<>(new DroneComparatorEarliestStartTime());
        allOriginalDrones = new ArrayList<>(n);

        launchedDrones = new TreeSet<>(new DroneComparatorEarliestEndTime());

        fillDrones(n, region);
    }

    public void fillDrones(int n, String region){
        try {
            FileReader dronesReader = DronesFiles.getDronesFile(region);
            JsonParser dronesJsonParser = new JsonParser();
            JsonArray dronesArray = (JsonArray) dronesJsonParser.parse(dronesReader);

            for (int i = 0; i < n; i++) {
                JsonObject droneJsonObj = (JsonObject)dronesArray.get(i);
                JsonArray start = (JsonArray)droneJsonObj.get("start");
                JsonArray end = (JsonArray)droneJsonObj.get("end");
                double startTime = droneJsonObj.get("start_time").getAsInt();

                Drone newDrone = new Drone(
                        new Vector2D(start.get(1).getAsInt(), start.get(0).getAsInt()),
                        new Vector2D(end.get(1).getAsInt(), end.get(0).getAsInt()),
                        startTime,
                        safetyZone,
                        params.speedPxS
                );

                dronesToLaunch.add(newDrone);
                allOriginalDrones.add(newDrone);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
