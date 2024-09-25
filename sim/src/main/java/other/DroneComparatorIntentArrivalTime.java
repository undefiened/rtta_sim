package other;

import org.jetbrains.annotations.NotNull;
import simulations.world.Drone;

import java.io.Serializable;
import java.util.Comparator;

public class DroneComparatorIntentArrivalTime implements Comparator<Drone>, Serializable {
    static final long serialVersionUID = 201L;

    @Override
    public int compare(@NotNull Drone d1, @NotNull Drone d2) {
        if(d1.getIntentArrivalTime() > d2.getIntentArrivalTime()){
            return 1;
        } else if(d1.equals(d2)) {
            return 0;
        } else if(d1.getIntentArrivalTime() < d2.getIntentArrivalTime()) {
            return -1;
        } else {
            return System.identityHashCode(d1) > System.identityHashCode(d2) ? 1 : -1;
        }
    }
}