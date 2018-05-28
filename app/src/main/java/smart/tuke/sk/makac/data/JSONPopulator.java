package smart.tuke.sk.makac.data;

import org.json.JSONObject;

public interface JSONPopulator {
    void populate(JSONObject data);

    JSONObject toJSON();
}
