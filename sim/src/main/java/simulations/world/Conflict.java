package simulations.world;

import math.geom2d.Vector2D;
import other.QuadraticEquationSolver;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by undefiened on 11/21/17.
 */
public class Conflict implements Serializable {
    static final long serialVersionUID = 22L;
    public Drone mainDrone, anotherDrone;
    private double timeStart = -1, timeEnd = -1, hoveringEndTime = -1;
    public boolean areInConflict;
    public double length;
    public double cpaTime, cpaDistance, squaredCpaDistance = -1;

    public Conflict(){
        this.mainDrone = null;
        this.anotherDrone = null;
    }

    public Conflict(Drone drone1, Drone drone2) throws Exception {
        this.mainDrone = drone1;
        this.anotherDrone = drone2;

        areInConflict = computeAreInConflict();
    }

    public void calculateStartAndEndTime() throws Exception {
        double startTime = getClampedStartTime();
        double endTime = getClampedEndTime();

        Vector2D drone1Start = mainDrone.positionAt(startTime);
        Vector2D drone2Start = anotherDrone.positionAt(startTime);

        Vector2D initialDistance = drone1Start.minus(drone2Start);
        Vector2D relativeVelocity = mainDrone.getVelocityVector().minus(anotherDrone.getVelocityVector());

        double[] time = calculateConflictTime(initialDistance, relativeVelocity, startTime);
        timeStart = Math.max(Math.min(time[0], time[1]), startTime);
        timeEnd = Math.min(Math.max(time[0], time[1]), endTime);

        length = timeEnd - timeStart;
    }

    public void calculateHoveringEndTime() throws Exception{
        double startTime = getStartTime();
//
//        Vector2D drone1Start = mainDrone.positionAt(startTime);
//        Vector2D drone2Start = anotherDrone.positionAt(startTime);
//
//        Vector2D initialDistance = drone1Start.minus(drone2Start);
//        Vector2D relativeVelocity = anotherDrone.getVelocityVector();
//
//        double[] time = calculateConflictTime(initialDistance, relativeVelocity, startTime);
//
//        hoveringEndTime = time[1];
//
//        if(hoveringEndTime < startTime){
//            throw new Exception("end time earlier than start time!");
//        }

        double delay = getNecessaryDelayTimeForFirstDrone();
        hoveringEndTime = startTime + delay;
    }

    private double[] calculateConflictTime(Vector2D initialDistance, Vector2D relativeVelocity, double startTime) throws Exception {
        double safetyDistanceSquared = Math.pow(getConflictDistance(), 2);
        double ss = initialDistance.dot(initialDistance);
        double vv = relativeVelocity.dot(relativeVelocity);
        double sv = initialDistance.dot(relativeVelocity);
        double a = vv;
        double b = 2 * sv;
        double c = ss - safetyDistanceSquared;
        QuadraticEquationSolver equation = new QuadraticEquationSolver(a, b, c);

        double timeFrom = startTime + equation.getMin();
        double timeTo = startTime + equation.getMax();

//        if(equation.getX1() < 0 && equation.getX2() < 0){
//            throw new Exception("Conflict started and ended before the start time");
//        }

        return new double[] {timeFrom, timeTo};
    }

    public double getNecessaryDelayTimeForFirstDrone() throws Exception {
        double delay = 1;

        while(areStillInConflict(delay)){
            delay += 1;
        }

        return Math.min(delay, anotherDrone.endTime-mainDrone.startTime + 0.01);
    }

    public boolean areStillInConflict(double delay) throws Exception {
        double cpaOfDelayedDrones = cpaTimeDelayedDrones(delay, 0);
        double n = getConflictDistance();
        double cpaDistanceSquared = getSquaredDistanceAtTimeForDelayedDrones(cpaOfDelayedDrones, delay, 0);
//        double cpaDistance = Math.sqrt(cpaDistanceSquared);

        if(cpaDistanceSquared <= n*n){
            return true;
        } else {
            return false;
        }
    }

    public double getConflictDistance(){
        return mainDrone.safetyZoneRadius + anotherDrone.safetyZoneRadius;
    }

    public double getDistanceAtTimeForDelayedDrones(double time, double firstDroneDelay, double secondDroneDelay) throws Exception {
        Vector2D initialDistance = getInitialDistanceForDelayedDrones(firstDroneDelay, secondDroneDelay);

        return initialDistance.plus(getRelativeVelocity().times(time - getClampedStartTime(firstDroneDelay, secondDroneDelay))).norm();
    }

    public double getSquaredDistanceAtTimeForDelayedDrones(double time, double firstDroneDelay, double secondDroneDelay) throws Exception {
        Vector2D initialDistance = getInitialDistanceForDelayedDrones(firstDroneDelay, secondDroneDelay);
        Vector2D dist = initialDistance.plus(getRelativeVelocity().times(time - getClampedStartTime(firstDroneDelay, secondDroneDelay)));
        return dist.dot(dist);
    }

    public void computeConflictBetween(Drone drone1, Drone drone2) throws Exception {
        this.mainDrone = drone1;
        this.anotherDrone = drone2;

        this.cpaTime = -1;
        this.cpaDistance = -1;
        this.squaredCpaDistance = -1;

        this.timeStart = -1;
        this.timeEnd = -1;
        this.length = -1;
        this.hoveringEndTime = -1;

        this.areInConflict = computeAreInConflict();
    }

    private boolean computeAreInConflict() throws Exception {
        if(mainDrone.startTime >= anotherDrone.endTime || mainDrone.endTime <= anotherDrone.startTime){
            return false;
        }

        cpaTime = cpaTime();
        squaredCpaDistance = getSquaredDistanceAtTimeForDelayedDrones(cpaTime, 0, 0);
        double n = getConflictDistance();

        if(squaredCpaDistance < n*n*0.99999999){
            cpaDistance = Math.sqrt(squaredCpaDistance);
            return true;
        } else {
            return false;
        }
    }

    public double cpaTime() throws Exception {
        return cpaTimeDelayedDrones(0, 0);
    }

    private double getClampedStartTime(){
        return getClampedStartTime(0, 0);
    }

    private double getClampedStartTime(double firstDroneDelay, double secondDroneDelay){
        return Math.max(mainDrone.startTime + firstDroneDelay, anotherDrone.startTime + secondDroneDelay);
    }

    private double getClampedEndTime(){
        return getClampedEndTime(0, 0);
    }

    private double getClampedEndTime(double firstDroneDelay, double secondDroneDelay){
        return Math.min(mainDrone.endTime + firstDroneDelay, anotherDrone.endTime + secondDroneDelay);
    }

    public Vector2D getInitialDistanceForDelayedDrones(double firstDroneDelay, double secondDroneDelay) throws Exception {
        double startTime = getClampedStartTime(firstDroneDelay, secondDroneDelay);
        Vector2D drone1Start = mainDrone.positionAtUnlimited(startTime - firstDroneDelay);
        Vector2D drone2Start = anotherDrone.positionAtUnlimited(startTime - secondDroneDelay);

        return drone1Start.minus(drone2Start);
    }

    private Vector2D getRelativeVelocity(){
        return mainDrone.getVelocityVector().minus(anotherDrone.getVelocityVector());
    }

    private double cpaTimeDelayedDronesUnclamped(double firstDroneDelay, double secondDroneDelay) throws Exception {
        double startTime = getClampedStartTime(firstDroneDelay, secondDroneDelay);
        Vector2D initialDistance = getInitialDistanceForDelayedDrones(firstDroneDelay, secondDroneDelay);

        Vector2D v = getRelativeVelocity();

        return startTime-(initialDistance.dot(v)/v.dot(v));
    }

    private double cpaTimeDelayedDrones(double firstDroneDelay, double secondDroneDelay) throws Exception {
        double startTime = getClampedStartTime(firstDroneDelay, secondDroneDelay);
        double endTime = getClampedEndTime(firstDroneDelay, secondDroneDelay);

        double cpaTime = cpaTimeDelayedDronesUnclamped(firstDroneDelay, secondDroneDelay);
        return Math.max(startTime, Math.min(endTime, cpaTime));
    }

    public double getStartTime() throws Exception {
        if(timeStart < 0){
            calculateStartAndEndTime();
        }
        return timeStart;
    }

    public double getEndTime() throws Exception {
        if(timeEnd < 0){
            calculateStartAndEndTime();
        }
        return timeEnd;
    }

    public double getHoveringEndTime() throws Exception {
        if(hoveringEndTime < 0){
            calculateHoveringEndTime();
        }
        return hoveringEndTime;
    }
}
