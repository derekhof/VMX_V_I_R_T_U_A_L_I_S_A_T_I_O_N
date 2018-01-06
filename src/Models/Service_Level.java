package Models;

import org.json.simple.JSONObject;

public class Service_Level {

    private String name;
    private int commision;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCommision() {
        return commision;
    }

    public void setCommision(int commision) {
        this.commision = commision;
    }

    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.name);
        jsonObject.put("commision", this.commision);
        return jsonObject.toJSONString();
    }
}
