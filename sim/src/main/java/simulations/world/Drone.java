package simulations.world;

import math.geom2d.Vector2D;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by undefiened on 11/21/17.
 */
public class Drone implements /*Comparable<Drone>, */Serializable, datastructures.Interval {
    static final long serialVersionUID = 21L;
    public Vector2D start;
    public Vector2D end;
    public double startTime, endTime;
    public double originalStartTime = -1;
    public double safetyZoneRadius;
    public double speed;
    public int layer = 0;
    public int maxLayer = 0;

    public Drone originalDrone;
    public boolean isOriginal = true;

    public Vector2D velocity;

    public ArrayList<Drone> resolvedDrones = new ArrayList<>();
    public ArrayList<Drone> hoverings = new ArrayList<>();
    public ArrayList<Drone> segments = new ArrayList<>();

    public double totalDelay = 0;
    public boolean wasInConflict = false;
    public boolean wasInCollision = false;


    public double intentArrivalTime = 0;
    public boolean cancelsBeforeStart = false;
    public double cancelDecisionTime = 0;
    public Long ID;
    public int type;


    public Drone(Vector2D start, Vector2D end, double startTime, double safetyZoneRadius, double speed) {
        this.start = start;
        this.end = end;
        this.startTime = startTime;
        this.originalStartTime = startTime;
        this.safetyZoneRadius = safetyZoneRadius;
        this.speed = speed;
        this.endTime = computeEndTime();
    }

    public Drone(Vector2D start, Vector2D end, double startTime, double endTime, double safetyZoneRadius, double speed) {
        this.start = start;
        this.end = end;
        this.startTime = startTime;
        this.originalStartTime = startTime;
        this.safetyZoneRadius = safetyZoneRadius;
        this.speed = speed;
        this.endTime = endTime;
    }

    public Drone() {
        //Constructor for creating empty mainDrone
    }

    public void delay(double time){
        totalDelay += time;
        startTime += time;
        endTime = computeEndTime();
    }

    public boolean wasDelayed(){
        return totalDelay > 0;
    }

    public boolean existsAt(double time){
        return time >= startTime && time <= endTime;
    }

    protected double computeEndTime(){
        return startTime + end.minus(start).norm()/speed;
    }

    public Vector2D positionAt(double time) throws Exception {
        if(time > endTime || time < startTime){
            throw new Exception("The mainDrone does not exist at the time");
        }

        return positionAtUnlimited(time);
    }

    public Vector2D positionAtUnlimited(double time) {
        return start.plus(getVelocityVector().times(time-startTime));
    }

    public Vector2D getVelocityVector(){
        if(velocity == null){
            velocity = end.minus(start).normalize().times(speed);
        }

        return velocity;
//        return end.minus(start).normalize().times(speed);
    }

    public Conflict getConflictWith(Drone anotherDrone) throws Exception {
        return new Conflict(this, anotherDrone);
    }

    public double getOriginalStartTime(){
         return originalStartTime;
    }

    public void setOriginalStartTime(double originalStartTime){
        this.originalStartTime = originalStartTime;
    }

//    public int compareTo(@NotNull Drone o) {
//        if(getOriginalStartTime() > o.getOriginalStartTime()){
//            return 1;
//        } else if(getOriginalStartTime() == o.getOriginalStartTime()) {
//            return 0;
//        } else {
//            return -1;
//        }
//    }

    public void setWasInConflict(){
        wasInConflict = true;
    }

    public boolean isOriginal(){
        return this.isOriginal;
    }

    public double azimuth(){
        return start.minus(end).angle();
    }

    @Override
    public int start() {
        return (int)Math.floor(startTime);
    }

    @Override
    public int end() {
        return (int)Math.ceil(endTime);
    }

    public void setOriginalDrone(Drone originalDrone){
        isOriginal = false;
        this.originalDrone = originalDrone;
        setOriginalStartTime(originalDrone.getOriginalStartTime());
    }

    public void setWasInCollision(){
        this.wasInCollision = true;
    }

    public Drone getOriginalDrone(){
        if(isOriginal){
            return this;
        } else {
            return this.originalDrone;
        }
    }

    public void addSegment(Drone segment){
        segments.add(segment);
    }

    public Drone getSegmentAtTime(double time){
        for (Drone segment :
                segments) {
            if (time >= segment.startTime && time <= segment.endTime) {
                return segment;
            }
        }

        return null;
    }

    public double getIntentArrivalTime() {
        return intentArrivalTime;
    }

    public void setIntentArrivalTime(double intentArrivalTime) {
        this.intentArrivalTime = intentArrivalTime;
    }

    public boolean isCancelsBeforeStart() {
        return cancelsBeforeStart;
    }

    public void setCancelsBeforeStart(boolean cancelsBeforeStart) {
        this.cancelsBeforeStart = cancelsBeforeStart;
    }

    public double getCancelDecisionTime() {
        return cancelDecisionTime;
    }

    public void setCancelDecisionTime(double cancelDecisionTime) {
        this.cancelDecisionTime = cancelDecisionTime;
    }

    public double getRTTATime(double RTTA) {
        if (this.getIntentArrivalTime() > this.startTime - RTTA) {
            return this.getIntentArrivalTime();
        } else {
            return this.startTime - RTTA;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}
