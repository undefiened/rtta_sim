import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import simulations.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Created by undefiened on 11/22/17.
 */
public class RunGDPSimulation {
    public static void main(String[] args) throws Exception {
        int[] ns = {10, 100, 1000, 5000, 10000, 20000, 30000, 40000, 50000}; // different scenarios with different numbers of drones to simulate

        double[] safetyZones = {50, 100, 150, 200, 250, 300}; // different scenarios with different safety zones radii

        double[] RTTAs = {60*5, 60*10, 60*15, 60*20, 60*25, 60*30, 60*35, 60*40, 60*45, 60*50, 60*55, 60*60,
                          60*65, 60*70, 60*75, 60*80, 60*85, 60*90, 60*95, 60*100, 60*105, 60*110, 60*115, 60*120,
                          60*125, 60*130, 60*135, 60*140, 60*145, 60*150, 60*155, 60*160, 60*165, 60*170, 60*175, 60*180,
                          60*185, 60*190, 60*195, 60*200, 60*205, 60*210, 60*215, 60*220, 60*225, 60*230, 60*235, 60*240,
        }; // different scenarios with different RTTA's

        double[] weatherCoefficients = {0.0, 1.0/(60.0*10), 1.0/(60.0*5), 1.0/(60.0*2.5), 1.0/(60.0*2), 1.0/(60.0*1.3333333333333333), 1.0/(60.0*1)}; // How much the safety radius is increased due to uncertainty at higher RTTA values. In meters per second

        double[] speedsMS = {25, }; // different scenarios with different drone speeds

        int numberOfDronesToTest = 50000;
        double probabilityOfPriority = 0.2; // How many flights are priority flights (police, etc.). By default should be 0

        String region = "nk";
        // 🚨 Use the "projected_crs" region instead of nk if you are using a data file with positions based on some projected CRS where 1 unit = 1 meter
        // String region = "projected_crs";

        for (double speedMS: speedsMS) {
            for (double weatherCoefficient: weatherCoefficients) {
                for (double safetyZone :
                        safetyZones) {
                    for (int n :
                            ns) {
                        JSONArray res = new JSONArray();
                        for (double RTTA : RTTAs) {
                            RTTAGDPSimulation simulationResult = simulate(n, safetyZone, RTTA, region, weatherCoefficient, speedMS, probabilityOfPriority);
                            JSONObject serializedResult = new JSONObject();

                            serializedResult.put("RTTA", RTTA);
                            serializedResult.put("drones", simulationResult.getSerializedResults());
                            res.add(serializedResult);
                        }


                        JSONObject obj = new JSONObject();
                        obj.put("safetyZone", safetyZone);
                        obj.put("n", n);
                        obj.put("res", res);
                        obj.put("weather_coefficient", weatherCoefficient);
                        obj.put("speed", speedMS);
                        obj.put("percentage_of_priority", probabilityOfPriority);
                        String truncatedWeatherCoefficient = String.format("%.2g", weatherCoefficient);
                        Files.write(Paths.get("results/RTTA_" + n + "_" + safetyZone + "_" + truncatedWeatherCoefficient + "_" + speedMS + "_" + probabilityOfPriority + ".json"), obj.toJSONString().getBytes());
                        System.gc();
                    }
                }
            }
        }
    }

    public static RTTAGDPSimulation simulate(int n, double safetyZone, double RTTA, String region, double weatherCoefficient, double speed, double probabilityOfPriority) throws Exception {
        System.out.println("-------------------------------------------");
        System.out.println(n + " " + safetyZone);
        System.out.println("-------------------------------------------");
//        conflictsNumberSimulation(n, safetyZone, region);
        return gdpSimulation(n, safetyZone, RTTA, region, weatherCoefficient, speed, probabilityOfPriority);
    }

    public static void conflictsNumberSimulation(int n, double safetyZone, String region) throws Exception {
        ConflictsNumberSimulation num = new ConflictsNumberSimulation(n, safetyZone, region);
        System.out.println("Pure # of conflicts = " + num.numberOfConflicts);
        System.out.println(
                "Pure # of drones in conflict = " +
                num.allOriginalDrones.stream().filter(
                        drone -> drone.wasInConflict
                ).count()
        );
        System.out.println(
                "Avg travel time = " +
                        num.allOriginalDrones.stream().mapToDouble(
                                drone -> (drone.endTime-drone.startTime)
                        ).sum()/num.allOriginalDrones.size()
        );
        saveResultsToFile(num, "num", region, n, safetyZone);
        System.out.println("---------");
    }

    public static Long countNumberOfConflictsWithDeconflictedSimulation(int n, int numberOfDronesToTest, double safetyZone, String region) throws Exception {
        ConflictProbabilitySimulation sim = new ConflictProbabilitySimulation(n, numberOfDronesToTest, safetyZone, region);
        return (long) sim.numberOfNewConflicts;
    }

    public static RTTAGDPSimulation gdpSimulation(int n, double safetyZone, double RTTA, String region, double weatherCoefficient, double speed, double probabilityOfPriority) throws Exception {
        System.out.println("Start of GDP simulation");
        int maxDelay = 60*15;
        RTTAGDPSimulation sim2 = new RTTAGDPSimulation(
                n, safetyZone, region,
                RTTA, 60*60*24, maxDelay,
                (x) -> 1/x,
                (x) -> {
//                    double maxProbabilityOfCancelling = 0.1;
                    double maxProbabilityOfCancelling = 0.1;
                    double minBoundary = 10 * 60;
                    double maxBoundary = 24 * 60 * 60;

//                    return 0.0;
                    if (x < minBoundary) {
                        return 0.0;
                    } else if (x > maxBoundary) {
                        return maxProbabilityOfCancelling;
                    } else {
                        return maxProbabilityOfCancelling * ((x - minBoundary)/(maxBoundary-minBoundary));
                    }
                },
                weatherCoefficient,
                speed,
                probabilityOfPriority
        );

        long cancelledScheduled = sim2.launchedDrones.stream().filter(x -> x.cancelsBeforeStart && x.originalStartTime - x.cancelDecisionTime <= RTTA).count();
        long overdelayedDrones = sim2.launchedDrones.stream().filter(x -> !x.cancelsBeforeStart && x.totalDelay > maxDelay).count();
        System.out.format("RTTA %.0f min %n", RTTA/60);
        System.out.format("Number of cancelled drones: %d%n", sim2.launchedDrones.stream().filter(x -> x.cancelsBeforeStart).count());
        System.out.format("Average delay: %f%n", sim2.launchedDrones.stream().mapToDouble(x -> x.totalDelay).average().getAsDouble());
        System.out.format("Number of overdelayed drones: %d%n", overdelayedDrones);
        System.out.format("Number of scheduled cancelled drones: %d%n", cancelledScheduled);
        System.out.format("Max start time: %f%n", sim2.launchedDrones.stream().filter(x -> !x.cancelsBeforeStart).mapToDouble(x -> x.startTime).max().getAsDouble());
        return sim2;
    }

    public static void saveResultsToFile(Object object, String filename, String region, int n, double safetyZone) throws IOException {
        //use buffering
        String folderName = "results";

        if(!region.equals("nk")){
            folderName = "results_bay";
        }

        OutputStream file = new FileOutputStream(folderName + "/" + filename + "_" + n + "_" + safetyZone + ".res");
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);
        try{
            output.writeObject(object);
        }
        finally{
            output.close();
        }
    }

}
