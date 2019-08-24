package network.marble.dataaccesslayer.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import network.marble.dataaccesslayer.exceptions.APIException;

import java.io.IOException;

public class ModelUtils {

    public static String getJsonAtRoot(String data, String rootName) throws IOException {
        if (data == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(data);
        if (node.has(rootName)) {
            return node.get(rootName).asText();
        } else return null;
    }

    public static void errorCheck(String data) throws APIException {
        if (data == null) throw new APIException("000", "Blank Response");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(data);
        } catch (IOException e) {
            throw new APIException("500", "JSON Parse Failure");
        }

        if (node != null && node.has("error")) {
            String code = node.get("error").textValue();
            JsonNode error = node.get("error");
            if (error.has("message")) {
                throw new APIException(code, error.get("message").textValue());
            } else {
                throw new APIException(code, "Failed to find error message");
            }
        }
    }
}
