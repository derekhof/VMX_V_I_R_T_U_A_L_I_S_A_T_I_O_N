package Models;

import org.json.simple.JSONObject;

public class Hypervisor {

    private String ip_address;
    private float diskspace;
    private float memory;
    private String company_id;


    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public float getDiskspace() {
        return diskspace;
    }

    public void setDiskspace(float diskspace) {
        this.diskspace = diskspace;
    }

    public float getMemory() {
        return memory;
    }

    public void setMemory(float memory) {
        this.memory = memory;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip_address", this.ip_address);
        jsonObject.put("diskspace", this.diskspace);
        jsonObject.put("memory", this.memory);
        jsonObject.put("company_id", this.company_id);

        return jsonObject.toJSONString();
    }
}
