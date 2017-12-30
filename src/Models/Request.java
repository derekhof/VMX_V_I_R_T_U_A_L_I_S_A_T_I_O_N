package Models;

import org.json.simple.JSONObject;

public class Request {

    private String command;
    private String params;
    private String hypervisor;
    private String company_id;
    private String username;
    private String vm;
    private String vm_id;
    private String status_request;

    public String getStatus_request() {
        return status_request;
    }

    public void setStatus_request(String status_request) {
        this.status_request = status_request;
    }

    public String getVm_id() {
        return vm_id;
    }

    public void setVm_id(String vm_id) {
        this.vm_id = vm_id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getVm() {
        return vm;
    }

    public void setVm(String vm) {
        this.vm = vm;
    }

    public String getHypervisor() {
        return hypervisor;
    }

    public void setHypervisor(String hypervisor) {
        this.hypervisor = hypervisor;
    }


    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // method returns a json string of the class
    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command", this.command);
        jsonObject.put("params", this.params);
        jsonObject.put("hypervisor", this.hypervisor);
        jsonObject.put("vm", this.vm);

        return jsonObject.toJSONString();

    }

}
