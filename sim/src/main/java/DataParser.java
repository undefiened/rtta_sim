import com.google.gson.Gson;
import simulations.*;
import simulations.world.Conflict;
import simulations.world.Drone;

import java.io.*;
import java.util.*;

public class DataParser {
    int[] ns;
    double[] safetyRadii;
    String region;

    public HashMap<Integer, HashMap<Double, Integer>> numberOfConflicts;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfDelayedDrones;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfHoverings;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfHoveredDrones;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfLayersDistributed;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfDivingsDistributed;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfLayersOptimal;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfDelayedFor10Percent;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfHoveredFor10Percent;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfAscendingConflictsHovering;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfAscendingConflictsOptimal;
    public HashMap<Integer, HashMap<Double, Integer>> numberOfAscendingConflictsDistributed;
//    public HashMap<Integer, HashMap<Double, Double>> lossOfEfficiencyGDP;
//    public HashMap<Integer, HashMap<Double, Double>> lossOfEfficiencyHovering;
    public HashMap<Integer, HashMap<Double, Double>> totalTime;
    public HashMap<Integer, HashMap<Double, Double>> totalDelay;
    public HashMap<Integer, HashMap<Double, Double>> totalHovering;
    public HashMap<Integer, HashMap<Double, HashMap<Integer, Double>>> deviationOfDirectionsDistributed;
    public HashMap<Integer, HashMap<Double, HashMap<Integer, Double>>> deviationOfDirectionsOptimal;
    public HashMap<Integer, HashMap<Double, HashMap<Integer, Integer>>> distributionOfDronesOptimal;
    public HashMap<Integer, HashMap<Double, HashMap<Integer, Integer>>> distributionOfDronesDistributed;

    public HashMap<Integer, HashMap<Double, ArrayList<Double>>> angleDifferences;
//    public HashMap<Integer, HashMap<Double, Double>> deviationOfDirectionsOptimal;

    public DataParser(int[] ns, double[] rs, String region){
        this.ns = ns;
        this.safetyRadii = rs;
        this.region = region;

        initResults();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        int[] ns = {10, 100, 1000, 5000, 10000, 20000, 30000, 40000, 50000};
        int[] ns = {10, 100, 1000, 5000, 10000, 20000, 30000, 40000, 50000};
//        int[] ns = {40000};
        double[] safetyZones = {50, 100, 150, 200, 250, 300};
//        double[] safetyZones = {200};

        String region = "nk";
        region = "bay";

        DataParser parser = new DataParser(ns, safetyZones, region);

        for (int n :
                ns) {
            for (double r :
                    safetyZones) {
                parser.parseResults(n, r);
            }
        }

        parser.saveResults();
    }

    public void initResults(){
        numberOfConflicts = new HashMap<>();
        numberOfDelayedDrones = new HashMap<>();
        numberOfHoveredDrones = new HashMap<>();
        numberOfHoverings = new HashMap<>();
        numberOfLayersDistributed = new HashMap<>();
        numberOfDivingsDistributed = new HashMap<>();
        numberOfLayersOptimal = new HashMap<>();
        numberOfHoveredFor10Percent = new HashMap<>();
        numberOfDelayedFor10Percent = new HashMap<>();

        distributionOfDronesOptimal = new HashMap<>();
        distributionOfDronesDistributed = new HashMap<>();

//        lossOfEfficiencyGDP = new HashMap<>();
//        lossOfEfficiencyHovering = new HashMap<>();
        totalTime = new HashMap<>();
        totalDelay = new HashMap<>();
        totalHovering = new HashMap<>();
        deviationOfDirectionsDistributed = new HashMap<>();
        deviationOfDirectionsOptimal = new HashMap<>();
        angleDifferences = new HashMap<>();

        numberOfAscendingConflictsHovering = new HashMap<>();
        numberOfAscendingConflictsOptimal = new HashMap<>();
        numberOfAscendingConflictsDistributed = new HashMap<>();
    }

    public void parseResults(int n, double r) throws IOException, ClassNotFoundException {
        System.out.println("Now parsing N=" + n + " r=" + r);
        setValue(numberOfConflicts, n, r, getNumber(n, r).numberOfConflicts);
        setValue(numberOfDelayedDrones, n, r, getNumberOfDelayed(n, r));

        setValue(numberOfDelayedFor10Percent, n, r, getDelayed10(n, r));

//        setValue(lossOfEfficiencyGDP, n, r, getLossOfEfficiencyGDP(n, r));
//        setValue(lossOfEfficiencyHovering, n, r, getLossOfEfficiencyHovering(n, r));
        setValue(totalTime, n, r, getTotalTime(n, r));
        setValue(totalDelay, n, r, getTotalDelay(n, r));
        setValue(angleDifferences, n, r, getAngleDifferences(n, r));


    }

    public void saveResults(){
        saveResultToFile(numberOfConflicts, "number_of_conflicts");
        saveResultToFile(numberOfDelayedDrones, "number_of_delayed_drones");
        saveResultToFile(numberOfHoverings, "number_of_hoverings");
        saveResultToFile(numberOfHoveredDrones, "number_of_hovered_drones");
        saveResultToFile(numberOfLayersDistributed, "number_of_layers_distributed");
        saveResultToFile(numberOfLayersOptimal, "number_of_layers_optimal");
        saveResultToFile(numberOfDivingsDistributed, "number_of_divings_distributed");
        saveResultToFile(numberOfHoveredFor10Percent, "number_of_hovered_10");
        saveResultToFile(numberOfDelayedFor10Percent, "number_of_delayed_10");
//        saveResultToFile(lossOfEfficiencyGDP, "loss_of_efficiency_gdp");
//        saveResultToFile(lossOfEfficiencyHovering, "loss_of_efficiency_hovering");
        saveResultToFile(totalTime, "total_time");
        saveResultToFile(totalDelay, "total_delay");
        saveResultToFile(totalHovering, "total_hovering");
        saveResultToFile(deviationOfDirectionsDistributed, "distribution_of_directions_distributed");
        saveResultToFile(deviationOfDirectionsOptimal, "distribution_of_directions_optimal");
        saveResultToFile(distributionOfDronesOptimal, "distribution_of_drones_optimal");
        saveResultToFile(distributionOfDronesDistributed, "distribution_of_drones_distributed");
        saveResultToFile(angleDifferences, "angle_differences");

        saveResultToFile(numberOfAscendingConflictsHovering, "number_of_ascending_conflicts_hovering");
        saveResultToFile(numberOfAscendingConflictsOptimal, "number_of_ascending_conflicts_optimal");
        saveResultToFile(numberOfAscendingConflictsDistributed, "number_of_ascending_conflicts_distributed");
    }

    public void saveResultToFile(Object results, String filename){
        Gson gson = new Gson();
        String json = gson.toJson(results);

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(getRegionFolder() + filename + ".json"));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int getDelayed10(int N, double r) throws IOException, ClassNotFoundException {
        RTTAGDPSimulation sim = getGDP(N, r);
        int num = 0;

        for (Drone drone :
                sim.launchedDrones) {
            if((drone.endTime - drone.startTime)*0.1 <= drone.totalDelay){
                num += 1;
            }
        }

        return num;
    }


    public ArrayList<Double> getAngleDifferences(int n, double r) throws IOException, ClassNotFoundException {
        ArrayList<Double> result = new ArrayList<>();
        ConflictsNumberSimulation sim = getNumber(n, r);

        for (Conflict conflict :
                sim.conflicts) {
            double angle = conflict.mainDrone.azimuth() - conflict.anotherDrone.azimuth();
            result.add(Math.abs(angle));
        }

        return result;
    }

    public int getNumberOfDelayed(int n, double r) throws IOException, ClassNotFoundException {
        int num = 0;

        for (Drone drone:
                getGDP(n, r).launchedDrones) {
            if(drone.wasDelayed()){
                num += 1;
            }
        }

        return num;
    }

    public double getLossOfEfficiencyGDP(int n, double r) throws IOException, ClassNotFoundException {
        double totalDelay = 0;
        double totalTime = 0;
        int numNaN = 0;


        for (Drone drone:
                getGDP(n, r).launchedDrones) {

            if(!Double.isNaN(drone.endTime)){
                if(drone.wasDelayed()){
                    totalDelay += drone.totalDelay;
                }

                totalTime += drone.endTime-drone.startTime;
            } else {
                numNaN += 1;
            }
        }

        System.out.println("numNaNGDP = " + numNaN);

        return totalDelay/totalTime;
    }

    public double getTotalDelay(int n, double r) throws IOException, ClassNotFoundException {
        double totalDelay = 0;

        for (Drone drone:
                getGDP(n, r).launchedDrones) {

                if(drone.wasDelayed()){
                    totalDelay += drone.totalDelay;
                }
        }

        return totalDelay;
    }

    public double getTotalTime(int n, double r) throws IOException, ClassNotFoundException {
        double totalTime = 0;

        for (Drone drone:
                getGDP(n, r).launchedDrones) {
                totalTime += drone.endTime-drone.startTime;
        }

        return totalTime;
    }

    public <T> void setValue(HashMap<Integer, HashMap<Double, T>> var, Integer n, Double r, T value){
        if(!var.containsKey(n)){
            fillVar(var, n);
        }
        var.get(n).put(r, value);
    }

    public <T> void fillVar(HashMap<Integer, HashMap<Double, T>> var, Integer N){
        if(!var.containsKey(N)){
            var.put(N, new HashMap<Double, T>());
        }
    }

    public RTTAGDPSimulation getGDP(int N, double r) throws IOException, ClassNotFoundException {
        return (RTTAGDPSimulation) parseFile("gdp", N, r);
    }

    public ConflictsNumberSimulation getNumber(int N, double r) throws IOException, ClassNotFoundException {
        return (ConflictsNumberSimulation) parseFile("num", N, r);
    }

    public String assembledFileName(String prefix, int N, double r){
        return prefix + "_" + N + "_" + r;
    }

    public Object parseFile(String prefix, int N, double r) throws IOException, ClassNotFoundException {
        Object recoveredSim = null;

        InputStream file = new FileInputStream(getRegionFolder() + assembledFileName(prefix, N, r) + ".res");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        try {
            recoveredSim = input.readObject();
        } finally {
            input.close();
        }

        return recoveredSim;
    }

    private String getRegionFolder(){
        String folder;
        if(region.equals("nk")){
            folder = "results/";
        } else {
            folder = "results_bay/";
        }

        return folder;
    }
}
