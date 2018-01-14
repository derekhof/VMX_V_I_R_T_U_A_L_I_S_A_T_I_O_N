package Models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class Invoice {

    private Company company;
    private List<Bill> bills;
    public String bill_period;
    public String invoice_date;

    public String getBill_period() {
        return bill_period;
    }

    public void setBill_period(String bill_period) {
        this.bill_period = bill_period;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }


    public String toJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("company_name", this.company.getCompany_name());
        jsonObject.put("zip_code", this.company.getZip_code());
        jsonObject.put("city", this.company.getCity());
        jsonObject.put("address", this.company.getAddress());
        jsonObject.put("housenumber", this.company.getHousenumber());
        jsonObject.put("bill_period", this.bill_period);
        jsonObject.put("invoice_date", this.invoice_date);
        JSONArray jsonArray = new JSONArray();
        for (Bill bill : bills){
            jsonArray.add(bill.toJsonObject());
        }

        jsonObject.put("bills", jsonArray);

        return jsonObject.toJSONString();
    }


}
