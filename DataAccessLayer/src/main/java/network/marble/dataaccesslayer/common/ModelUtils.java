package network.marble.dataaccesslayer.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import network.marble.dataaccesslayer.exceptions.APIException;

public class ModelUtils<T> {

    public ModelUtils() {}

    public String getJsonAtRoot(String data, String rootName) {
        //Bukkit.getLogger().info("data returned "+data);
        if (data == null) return null;
        JsonElement root = new JsonParser().parse(data);
        if (root.isJsonObject() && root.getAsJsonObject().has(rootName)) return root.getAsJsonObject().get(rootName).toString();
        return null;
    }

    public void errorCheck(String data) throws APIException {
        if (data == null) throw new APIException("000", "Blank Response");
        JsonElement root = new JsonParser().parse(data);
        if (root.isJsonObject() && root.getAsJsonObject().has("error")) {
            String code = root.getAsJsonObject().get("error").toString();
            JsonElement message = root.getAsJsonObject().get("message");
            throw new APIException(code, message != null ? message.toString() : "Failed to find error message");
        }
    }
}
