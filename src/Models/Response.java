package Models;

import org.json.simple.JSONObject;

public class Response {

    private String status;
    private String payload;


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
}
