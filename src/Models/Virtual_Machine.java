package Models;

import org.json.simple.JSONObject;

import java.sql.Date;

public class Virtual_Machine {

    private String vm_name;
    private String vm_id;
    private Date create_date;
    private Date delete_date;
    private String hypervisor_ip;
    private String template_id;

    public String getVm_name() {
        return vm_name;
    }

    public void setVm_name(String vm_name) {
        this.vm_name = vm_name;
    }

    public String getHypervisor_ip() {
        return hypervisor_ip;
    }

    public void setHypervisor_ip(String hypervisor_ip) {
        this.hypervisor_ip = hypervisor_ip;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getVm_id() {
        return vm_id;
    }

    public void setVm_id(String vm_id) {
        this.vm_id = vm_id;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getDelete_date() {
        return delete_date;
    }

    public void setDelete_date(Date delete_date) {
        this.delete_date = delete_date;
    }

    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vm_name", this.vm_name);
        jsonObject.put("vm_id", this.vm_id);
        jsonObject.put("hypervisor_ip", this.hypervisor_ip);
        jsonObject.put("template_id", this.template_id);
        jsonObject.put("create_date", this.create_date);
        jsonObject.put("delete_date", this.delete_date);
        return jsonObject.toJSONString();
    }
}
