package Models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Response {

    private String status;
    private String payload;
    private JSONArray payload_array;

    public JSONArray getPayload_array() {
        return payload_array;
    }

    public void setPayload_array(JSONArray payload_array) {
        this.payload_array = payload_array;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String toJsonString(){
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("status", this.status);
        jsonobject.put("payload", this.payload);

        return jsonobject.toJSONString();
    }



    public String toJsonStringWithArray(){
        JSONObject jsonobject = new JSONObject();
        jsonobject.put("status", this.status);
        jsonobject.put("payload", this.payload_array);

        return jsonobject.toJSONString();
    }


}
