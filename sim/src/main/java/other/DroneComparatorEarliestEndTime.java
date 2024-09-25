package other;

import simulations.world.Drone;

import java.io.Serializable;
import java.util.Comparator;

public class DroneComparatorEarliestEndTime implements Comparator<Drone>, Serializable {
    static final long serialVersionUID = 201L;

    @Override
    public int compare(Drone o1, Drone o2) {
        if(o1.endTime > o2.endTime){
            return 1;
        } else if(o1.equals(o2)) {
            return 0;
        } else if(o1.endTime < o2.endTime){
            return -1;
        } else {
            return System.identityHashCode(o1) > System.identityHashCode(o2) ? 1 : -1;
        }
    }
}
