package other;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by undefiened on 5/25/17.
 */
public class AllParams implements Serializable{
    static final long serialVersionUID = 42L;

    private static AllParams instance = null;
    String region;

    private AllParams(){
    }

    public void setRegion(String region){
        this.region = region;
    }

    public static AllParams getInstance() {
        if(instance == null){
            instance = new AllParams();
        }
        return instance;
    }

    public static Params get() throws Exception {
        String region = AllParams.getInstance().region;
        if(region == null){
            throw new Exception("Region must be defined before");
        }
        return get(region);
    }

    public static Params get(String region) {
        HashMap<String, Params> allParams = new HashMap<>();
        allParams.put("bay", new Params(6.7f, 90f));
        allParams.put("bay_noise", new Params(6.7f, 60f));
        allParams.put("nk", new Params(131, 90f, 1));
        allParams.put("nk_20ms", new Params(131, 72f, 1));
        allParams.put("nk_noise", new Params(131, 60f));
        allParams.put("projected_crs", new Params(1, 30f));
        allParams.put("test_area", new Params(1, 30f));

        return allParams.get(region);
    }
}
