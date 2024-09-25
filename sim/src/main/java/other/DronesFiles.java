package other;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by undefiened on 11/22/17.
 */
public class DronesFiles implements Serializable{
    static final long serialVersionUID = 52L;
    public static FileReader getDronesFile(String region) throws FileNotFoundException {
        String dronesJson;
        if(Objects.equals(region, "nk")){
            dronesJson = "./data/norrkoping_drones.json";
        } else {
            dronesJson = "/home/undefiened/simulations/data/drones_bay_100k.json";
        }

        return new FileReader(dronesJson);
    }
}
