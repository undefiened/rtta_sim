package other;

import org.jetbrains.annotations.NotNull;
import simulations.world.Drone;

import java.io.Serializable;
import java.util.Comparator;


public class DroneComparatorEarliestRTTATime implements Comparator<Drone>, Serializable {
    static final long serialVersionUID = 201L;

    public final double RTTA;

    public DroneComparatorEarliestRTTATime(double RTTA) {
        this.RTTA = RTTA;
    }

    @Override
    public int compare(@NotNull Drone d1, @NotNull Drone d2) {
        if(d1.getRTTATime(this.RTTA) > d2.getRTTATime(this.RTTA)){
            return 1;
        } else if(d1.equals(d2)) {
            return 0;
        } else if(d1.getRTTATime(this.RTTA) < d2.getRTTATime(this.RTTA)) {
            return -1;
        } else {
            return System.identityHashCode(d1) > System.identityHashCode(d2) ? 1 : -1;
        }
    }
}