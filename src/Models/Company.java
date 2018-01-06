package Models;

import org.json.simple.JSONObject;

import java.sql.Date;

public class Company
{
    private String company_id;
    private String company_name;
    private String zip_code;
    private String city;
    private String address;
    private int housenumber;
    private String website;
    private Date create_date;


    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public int getHousenumber() {
        return housenumber;
    }

    public void setHousenumber(int housenumber) {
        this.housenumber = housenumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    // Method returns a json string of the object
    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("company_id", this.company_id);
        jsonObject.put("company_name", this.company_name);
        jsonObject.put("zip_code", this.zip_code);
        jsonObject.put("city", this.city);
        jsonObject.put("address", this.address);
        jsonObject.put("housenumber", this.housenumber);
        jsonObject.put("website", this.website);
        return jsonObject.toJSONString();
    }



}
