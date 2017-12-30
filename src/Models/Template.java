package Models;

import org.json.simple.JSONObject;

public class Template {


    private String template_id;
    private String operating_system;
    private float memory;
    private float diskspace;
    private String specification;


    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getOperating_system() {
        return operating_system;
    }

    public void setOperating_system(String operating_system) {
        this.operating_system = operating_system;
    }

    public float getMemory() {
        return memory;
    }

    public void setMemory(float memory) {
        this.memory = memory;
    }

    public float getDiskspace() {
        return diskspace;
    }

    public void setDiskspace(float diskspace) {
        this.diskspace = diskspace;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("template_id", this.template_id);
        jsonObject.put("operating_system", this.operating_system);
        jsonObject.put("memory", this.memory);
        jsonObject.put("diskspace", this.diskspace);
        jsonObject.put("specification", this.specification);
        return jsonObject.toJSONString();
    }
}


