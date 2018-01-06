package Models;

import org.json.simple.JSONObject;

import java.sql.Date;

public class Bill {

    private String vm_name;
    private String operating_system;
    private float price;
    private String service_level;
    private int commision;
    private float month_costs_vm;
    private String billing_period_date;
    private float month_days;


    public String getVm_name() {
        return vm_name;
    }

    public void setVm_name(String vm_name) {
        this.vm_name = vm_name;
    }

    public String getService_level() {
        return service_level;
    }

    public void setService_level(String service_level) {
        this.service_level = service_level;
    }

    public String getBilling_period_date() {
        return billing_period_date;
    }

    public void setBilling_period_date(String billing_period_date) {
        this.billing_period_date = billing_period_date;
    }

    public String getOperating_system() {
        return operating_system;
    }

    public void setOperating_system(String operating_system) {
        this.operating_system = operating_system;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getMonth_costs_vm() {
        return month_costs_vm;
    }

    public void setMonth_costs_vm(float month_costs_vm) {
        this.month_costs_vm = month_costs_vm;
    }

    public float getMonth_days() {
        return month_days;
    }

    public void setMonth_days(float month_days) {
        this.month_days = month_days;
    }

    public int getCommision() {
        return commision;
    }

    public void setCommision(int commision) {
        this.commision = commision;
    }

    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vm_name",this.vm_name);
        jsonObject.put("operating_system",this.operating_system);
        jsonObject.put("price",this.price);
        jsonObject.put("Service level", this.service_level);
        jsonObject.put("commision", this.commision);
        jsonObject.put("billing_period", this.billing_period_date);
        jsonObject.put("month_days",this.month_days);
        jsonObject.put("month_costs_vm",this.month_costs_vm);
        return jsonObject.toJSONString();
    }
}
