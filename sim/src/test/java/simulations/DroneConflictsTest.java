package simulations;

import junit.framework.TestCase;
import math.geom2d.Vector2D;
import other.DroneComparatorEarliestEndTime;
import simulations.world.Conflict;
import simulations.world.Drone;

import java.util.TreeSet;

/**
 * Created by undefiened on 11/21/17.
 */
public class DroneConflictsTest extends TestCase {
    public void testEndTime() {
        Drone drone1 = new Drone(
                new Vector2D(0, 0), new Vector2D(0, 1),
                0, 0.1, 1
        );

        assertEquals(1.0, drone1.endTime);

        Drone drone2 = new Drone(
                new Vector2D(0, 0), new Vector2D(1, 1),
                0, 0.1, 1
        );

        assertEquals(1.4142135623730951, drone2.endTime);
    }

    public void testForIntersection() {
        Drone drone1 = new Drone(
                new Vector2D(0, 0), new Vector2D(1, 1),
                0, 0.1, 1
        );

        Drone drone2 = new Drone(
                new Vector2D(1, 0), new Vector2D(0, 1),
                0, 0.1, 1
        );

        try {
            Conflict conflict = drone1.getConflictWith(drone2);
            assertEquals(0.7071067811865476, conflict.cpaTime());
            assertTrue(conflict.areInConflict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testForNotIntersectionInTime() {
        Drone drone1 = new Drone(
                new Vector2D(0, 0), new Vector2D(1, 1),
                0, 0.1, 1
        );

        Drone drone2 = new Drone(
                new Vector2D(1, 0), new Vector2D(0, 1),
                10, 0.1, 1
        );

        try {
            Conflict conflict = drone1.getConflictWith(drone2);
            assertFalse(conflict.areInConflict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testForNotIntersectionInSpace() {
        Drone drone1 = new Drone(
                new Vector2D(0, 0), new Vector2D(0, 1),
                0, 0.1, 1
        );

        Drone drone2 = new Drone(
                new Vector2D(1, 0), new Vector2D(1, 1),
                0, 0.1, 1
        );

        try {
            Conflict conflict = drone1.getConflictWith(drone2);
            assertFalse(conflict.areInConflict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testRecommendedDelayTime() throws Exception {
        Vector2D from1 = new Vector2D(965.0, 1165.0);
        Vector2D to1 = new Vector2D(1034.0, 1464.0);

        Vector2D from2 = new Vector2D(1213.0, 1415.0);
        Vector2D to2 = new Vector2D(705.0, 915.0);

        Drone drone1 = new Drone(
                from1, to1,
                4798.0, 3.9, 1.625
        );

        Drone drone2 = new Drone(
                from2, to2,
                4585.0, 3.9, 1.625
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        double delay = conflict.getNecessaryDelayTimeForFirstDrone();

        drone1.delay(delay);

        Conflict conflict2 = drone1.getConflictWith(drone2);
        assertFalse(conflict2.areInConflict);
    }


    public void testRecommendedDelayTime2() throws Exception {
        Vector2D from1 = new Vector2D(790.0, 923.0);
        Vector2D to1 = new Vector2D(1042.0, 1368.0);

        Vector2D from2 = new Vector2D(1604.0, 1230.0);
        Vector2D to2 = new Vector2D(954.0, 1207.0);

        Drone drone1 = new Drone(
                from1, to1,
                950.0, 6.5, 1.625
        );

        Drone drone2 = new Drone(
                from2, to2,
                760.0, 6.5, 1.625
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        double delay = conflict.getNecessaryDelayTimeForFirstDrone();
        System.out.println(delay);

        drone1.delay(delay);

        Conflict conflict2 = drone1.getConflictWith(drone2);
        assertFalse(conflict2.areInConflict);
    }

// FIX me
    public void testRecommendedDelayTime3() throws Exception {
        Vector2D from1 = new Vector2D(948.0, 1213.0);
        Vector2D to1 = new Vector2D(1007.0, 1310.0);

        Vector2D from2 = new Vector2D(988.0, 1293.0);
        Vector2D to2 = new Vector2D(760.0, 875.0);

        Drone drone1 = new Drone(
                from1, to1,
                5294.0, 6.5, 1.625
        );

        Drone drone2 = new Drone(
                from2, to2,
                5298.627182550973, 6.5, 1.625
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        double delay = conflict.getNecessaryDelayTimeForFirstDrone();
        System.out.println(delay);
        drone1.delay(delay);

        Conflict conflict2 = drone1.getConflictWith(drone2);
        assertFalse(conflict2.areInConflict);
    }

    public void testRecommendedDelayTime4() throws Exception {
        Vector2D from1 = new Vector2D(1014.0, 1313.0);
        Vector2D to1 = new Vector2D(737.0, 1756.0);

        Vector2D from2 = new Vector2D(993.0, 1364.0);
        Vector2D to2 = new Vector2D(1226.0, 1012.0);

        Drone drone1 = new Drone(
                from1, to1,
                6375.0, 6.5, 1.625
        );

        Drone drone2 = new Drone(
                from2, to2,
                6377.9245357576965, 6.5, 1.625
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        double delay = conflict.getNecessaryDelayTimeForFirstDrone();
        System.out.println(delay);
        drone1.delay(delay);

        Conflict conflict2 = drone1.getConflictWith(drone2);
        assertFalse(conflict2.areInConflict);
    }

    public void testRecommendedDelayTime5() throws Exception {
        Vector2D from1 = new Vector2D(910.0, 1173.0);
        Vector2D to1 = new Vector2D(775.0, 893.0);

        Vector2D from2 = new Vector2D(918.0, 1182.0);
        Vector2D to2 = new Vector2D(41.0, 1045.0);

        Drone drone1 = new Drone(
                from1, to1,
                32819.0, 6.5, 1.625
        );

        Drone drone2 = new Drone(
                from2, to2,
                32817.16488374175, 6.5, 1.625
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        double delay = conflict.getNecessaryDelayTimeForFirstDrone();
        System.out.println(delay);
        drone1.delay(delay);

        Conflict conflict2 = drone1.getConflictWith(drone2);
        assertFalse(conflict2.areInConflict);
    }

    public void testRecommendedDelayTime6() throws Exception {
        Vector2D from1 = new Vector2D(1142.0, 1400.0);
        Vector2D to1 = new Vector2D(1103.0, 1371.0);

        Vector2D from2 = new Vector2D(1141.0, 1396.0);
        Vector2D to2 = new Vector2D(1051.0, 1167.0);

        Drone drone1 = new Drone(
                from1, to1,
                47.0, 6.5, 1.625
        );

        Drone drone2 = new Drone(
                from2, to2,
                53.667111360952, 6.5, 1.625
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        double delay = conflict.getNecessaryDelayTimeForFirstDrone();
        System.out.println(delay);
        drone1.delay(delay);

        Conflict conflict2 = drone1.getConflictWith(drone2);
        assertFalse(conflict2.areInConflict);
    }

    public void testHovering() throws Exception {
        Vector2D from1 = new Vector2D(793.0, 930.0);
        Vector2D to1 = new Vector2D(753.0, 1719.0);

        Vector2D from2 = new Vector2D(673.0, 1421.0);
        Vector2D to2 = new Vector2D(859.5348473286939, 1087.9718170728554);

        Drone drone1 = new Drone(
                from1, to1,
                18.0, 13.0, 1.625
        );

        Drone drone2 = new Drone(
                from2, to2,
                18.0, 13.0, 1.625
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        assertTrue(conflict.areInConflict);
        double conflictStart = conflict.getStartTime();
        double conflictEnd = conflict.getEndTime();

        assertEquals(26, conflict.getDistanceAtTimeForDelayedDrones(conflictStart, 0, 0), 0.001);
        assertEquals(26, conflict.getDistanceAtTimeForDelayedDrones(conflictEnd, 0, 0), 0.001);
        assertEquals(14.376482453776212, conflict.getNecessaryDelayTimeForFirstDrone());
        assertEquals(172.61258046380365, conflictStart);
        assertEquals(186.98906291757987, conflict.getHoveringEndTime());
        assertEquals(182.57667929277042, conflictEnd);

        Drone endSegment = new Drone(
                conflict.mainDrone.positionAt(conflict.getStartTime()), conflict.mainDrone.end,
                conflict.getHoveringEndTime()+0.1, conflict.mainDrone.safetyZoneRadius, conflict.mainDrone.speed);

        Conflict testEndSegment = endSegment.getConflictWith(drone2);

        assertFalse(testEndSegment.areInConflict);
    }

    public void testInitialDistance() throws Exception {
        Drone drone1 = new Drone(
                new Vector2D(0, 0),
                new Vector2D(1, 0),
                0, 0, 1
        );

        Drone drone2 = new Drone(
                new Vector2D(1, 0),
                new Vector2D(0, 0),
                0, 0, 1
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        Vector2D dist = conflict.getInitialDistanceForDelayedDrones(0, 0);
        assertEquals(1.0, dist.norm());

        Vector2D dist2 = conflict.getInitialDistanceForDelayedDrones(0.5, 0);
        assertEquals(0.5, dist2.norm());

        Vector2D dist3 = conflict.getInitialDistanceForDelayedDrones(0, 0.5);
        assertEquals(0.5, dist3.norm());

        Vector2D dist4 = conflict.getInitialDistanceForDelayedDrones(0.5, 0.5);
        assertEquals(1.0, dist4.norm());
    }

    public void testInitialDistance2() throws Exception {
        Drone drone1 = new Drone(
                new Vector2D(0, 0),
                new Vector2D(1, 0),
                0, 0, 1
        );

        Drone drone2 = new Drone(
                new Vector2D(1, 0),
                new Vector2D(0, 0),
                0, 0, 2
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        Vector2D dist = conflict.getInitialDistanceForDelayedDrones(0, 0);
        assertEquals(1.0, dist.norm());

        Vector2D dist2 = conflict.getInitialDistanceForDelayedDrones(0.5, 0);
        assertEquals(0.0, dist2.norm());

        Vector2D dist3 = conflict.getInitialDistanceForDelayedDrones(0, 0.5);
        assertEquals(0.5, dist3.norm());

        Vector2D dist4 = conflict.getInitialDistanceForDelayedDrones(0.5, 0.5);
        assertEquals(1.0, dist4.norm());
    }

    public void testInitialDistance3() throws Exception {
        Drone drone1 = new Drone(
                new Vector2D(0, 0),
                new Vector2D(1, 0),
                0, 0, 1
        );

        Drone drone2 = new Drone(
                new Vector2D(1, 0),
                new Vector2D(0, 0),
                1, 0, 2
        );

        Conflict conflict = drone1.getConflictWith(drone2);
        Vector2D dist = conflict.getInitialDistanceForDelayedDrones(0, 0);
        assertEquals(0.0, dist.norm());

        Vector2D dist2 = conflict.getInitialDistanceForDelayedDrones(0.5, 0);
        assertEquals(0.5, dist2.norm());

        Vector2D dist3 = conflict.getInitialDistanceForDelayedDrones(0, 0.5);
        assertEquals(0.5, dist3.norm());

        Vector2D dist4 = conflict.getInitialDistanceForDelayedDrones(0.5, 0.5);
        assertEquals(0.0, dist4.norm());
    }

    public void testHoveringRecoversCount() throws Exception {
        Vector2D from1 = new Vector2D(0, 0);
        Vector2D to1 = new Vector2D(1, 0);

        Vector2D from2 = new Vector2D(1, 0);
        Vector2D to2 = new Vector2D(0, 0);

        Vector2D from3 = new Vector2D(0.6, 0);
        Vector2D to3 = new Vector2D(0, 0);

        HoveringSimulation sim = new HoveringSimulation(3, 1, "test_area");
        sim.dronesToLaunch.clear();
        sim.launchedDrones.clear();
        sim.dronesToLaunch.add(new Drone(
                from1, to1,
                0.0, 0.1, 1.0
        ));
        sim.dronesToLaunch.add(new Drone(
                from2, to2,
                0.0, 0.1, 1.0
        ));
        sim.dronesToLaunch.add(new Drone(
                from3, to3,
                1.399, 0.1, 1.0
        ));

        sim.run();

        assertEquals(1, sim.numberOfRecoversToAConflict);
    }

    public void testNoHoveringRecoverProblem() throws Exception {
        Vector2D from1 = new Vector2D(0, 0);
        Vector2D to1 = new Vector2D(1, 0);

        Vector2D from2 = new Vector2D(1, 0);
        Vector2D to2 = new Vector2D(0, 0);

//        Vector2D from3 = new Vector2D(0.5, 0.5);
//        Vector2D to3 = new Vector2D(0.5, 0.5);

        HoveringSimulation sim = new HoveringSimulation(2, 1, "test_area");
        sim.dronesToLaunch.clear();
        sim.launchedDrones.clear();
        sim.dronesToLaunch.add(new Drone(
                from1, to1,
                0.0, 0.1, 1.0
        ));
        sim.dronesToLaunch.add(new Drone(
                from2, to2,
                0.0, 0.1, 1.0
        ));
//        sim.dronesToLaunch.add(new Drone(
//                from3, to3,
//                0.51, 0.1, 1.0
//        ));

        sim.run();

        assertEquals(0, sim.numberOfRecoversToAConflict);
    }

    public void testDistributedDescendingConflictsCount() throws Exception {
        Vector2D from1 = new Vector2D(0, 0);
        Vector2D to1 = new Vector2D(1, 0);

        Vector2D from2 = new Vector2D(1, 0);
        Vector2D to2 = new Vector2D(0, 0);

        Vector2D from3 = new Vector2D(0.6, 0);
        Vector2D to3 = new Vector2D(0, 0);

        OnDemandDescentSimulation sim = new OnDemandDescentSimulation(2, 1, "test_area");
        sim.dronesToLaunch.clear();
        sim.launchedDrones.clear();
        sim.dronesToLaunch.add(new Drone(
                from1, to1,
                0.0, 0.1, 1.0
        ));
        sim.dronesToLaunch.add(new Drone(
                from2, to2,
                0.0, 0.1, 1.0
        ));

        sim.layers.add(new TreeSet<>());
        sim.layers.get(1).add(new Drone(
                from3, to3,
                0.398, 0.1, 1.0
        ));

        sim.run();

        assertEquals(1, sim.numberOfDivingsToAConflict);
    }

    public void testDistributedNoDescendingConflicts() throws Exception {
        Vector2D from1 = new Vector2D(0, 0);
        Vector2D to1 = new Vector2D(1, 0);

        Vector2D from2 = new Vector2D(1, 0);
        Vector2D to2 = new Vector2D(0, 0);

//        Vector2D from3 = new Vector2D(0.6, 0);
//        Vector2D to3 = new Vector2D(0, 0);

        OnDemandDescentSimulation sim = new OnDemandDescentSimulation(2, 1, "test_area");
        sim.dronesToLaunch.clear();
        sim.launchedDrones.clear();
        sim.dronesToLaunch.add(new Drone(
                from1, to1,
                0.0, 0.1, 1.0
        ));
        sim.dronesToLaunch.add(new Drone(
                from2, to2,
                0.0, 0.1, 1.0
        ));

        sim.layers.add(new TreeSet<>());
//        sim.layers.get(1).add(new Drone(
//                from3, to3,
//                0.398, 0.1, 1.0
//        ));

        sim.run();

        assertEquals(0, sim.numberOfDivingsToAConflict);
    }

    public void testDelay() throws Exception {
        Vector2D from1 = new Vector2D(1386.0, 1521.0);
        Vector2D to1 = new Vector2D(1212.0, 1408.0);
        double startTime1 = 22868.0;

        Vector2D from2 = new Vector2D(1231.0, 1024.0);
        Vector2D to2 = new Vector2D(1342.0, 1554.0);
        double startTime2 = 22566.32768052473;

        double safetyZone = 19.5;
        double speed = 1.625;

        Drone d1 = new Drone(
                from1, to1,
                startTime1, safetyZone, speed
        );

        Drone d2 = new Drone(
                from2, to2,
                startTime2, safetyZone, speed
        );

        Conflict conflict = d1.getConflictWith(d2);
        assertTrue(conflict.areInConflict);
        assertEquals(7.0, conflict.getNecessaryDelayTimeForFirstDrone());

        d1.delay(conflict.getNecessaryDelayTimeForFirstDrone());
        conflict = d1.getConflictWith(d2);
        assertFalse(conflict.areInConflict);

        d1.delay(-1);
        conflict = d1.getConflictWith(d2);
        assertTrue(conflict.areInConflict);

    }

    public void testSplit() throws Exception {
        Vector2D from1 = new Vector2D(0.0, 0.0);
        Vector2D to1 = new Vector2D(1.0, 0.0);
        double startTime1 = 0.0;

        Drone drone1 = new Drone(from1, to1, startTime1, 0.0, 1.0);

        HoveringSimulation.SplittedDrone droneSplit1 = new HoveringSimulation.SplittedDrone(drone1, 0.5, 1);

        assertEquals(0.0, droneSplit1.startSegment.startTime, 0.0001);
        assertEquals(0.5, droneSplit1.startSegment.endTime, 0.0001);
        assertEquals(0.0, droneSplit1.startSegment.start.getX(),0.0001);
        assertEquals(0.0, droneSplit1.startSegment.start.getY(),0.0001);
        assertEquals(0.5, droneSplit1.startSegment.end.getX(), 0.0001);
        assertEquals(0.0, droneSplit1.startSegment.end.getY(), 0.0001);

        assertEquals(1.01, droneSplit1.endSegment.startTime, 0.0001);
        assertEquals(1.51, droneSplit1.endSegment.endTime,0.0001);
        assertEquals(0.5, droneSplit1.endSegment.start.getX(),0.0001);
        assertEquals(0.0, droneSplit1.endSegment.start.getY(),0.0001);
        assertEquals(1.0, droneSplit1.endSegment.end.getX(),0.0001);
        assertEquals(0.0, droneSplit1.endSegment.end.getY(),0.0001);

        assertEquals(0.5, droneSplit1.hoveringPart.startTime, 0.0001);
        assertEquals(1.0, droneSplit1.hoveringPart.endTime, 0.0001);
        assertEquals(0.5, droneSplit1.hoveringPart.start.getX(),0.0001);
        assertEquals(0.0, droneSplit1.hoveringPart.start.getY(),0.0001);
        assertEquals(0.5, droneSplit1.hoveringPart.end.getX(),0.0001);
        assertEquals(0.0, droneSplit1.hoveringPart.end.getY(),0.0001);
    }

    public void testComparator(){
        TreeSet<Drone> drones = new TreeSet<>(new DroneComparatorEarliestEndTime());
        Drone drone1 = new Drone();
        drone1.startTime = 100;
        drone1.endTime = 200;

        Drone drone2 = new Drone();
        drone2.startTime = 150;
        drone2.endTime = 200;

        assertTrue(drones.add(drone1));
        assertFalse(drones.contains(drone2));
        assertTrue(drones.add(drone2));
    }
}