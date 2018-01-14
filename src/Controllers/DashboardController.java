package Controllers;

import Data_Access_Objects.Dashboard_Dao;
import Models.*;
import Provisioning_API.Provisioning_Server;
import Send_Invoice.Send_Invoice;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DashboardController extends HttpServlet {

    private final String REQUEST_INVALID_PARMS = "The request contains invalid parameters.";
    private final String NOSESSION = "NOSESSION";
    private final String DUPLICATE = "DUPLICATE";
    private final String FAILED = "FAILED";
    private final String SUCCEED = "SUCCEED";
    private boolean payload_is_array = false;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        System.out.println("Dashboard controller: HTTP POST Request");    // debug comment

        // declerations
        String jsonStringRequest;
        Dashboard_Dao dashboard_dao;
        Response response_message = new Response();
        Request request_message;
        Provisioning_Server provisioning_server = new Provisioning_Server();

        // check if the user has a actual session, if so, go further
        HttpSession session = request.getSession(false);
        if (session != null) {

            // Get POST data en write it in a String
            try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                jsonStringRequest = br.readLine();
                System.out.println("Dashboard controller: following Json String received: " + jsonStringRequest);    // debug comment
            }

            // Process the data
            if (jsonStringRequest != null) {
                try {
                    // new instance of dashboard data access object
                    dashboard_dao = new Dashboard_Dao();

                    // Convert json object to Request object
                    request_message = fillRequestData((JSONObject) JSONValue.parseWithException(jsonStringRequest));

                    // put session info in request object
                    request_message.setUsername(session.getAttribute("username").toString());

                    // check if the command if filled
                    if (request_message.getCommand() != "") {

                        // check the chosen command
                        switch (request_message.getCommand()) {
                            case "list-vms":
                                // Set payload to array
                                payload_is_array = true;

                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command list-vms");
                                request_message = dashboard_dao.listVMs(request_message);
                                response_message = provisioning_server.listVms(request_message);

                                if (response_message.getStatus().equals(SUCCEED)) {
                                    // Add additional data from database to response payload
                                    response_message = ListVmData(response_message, request_message.getCompany_id(), dashboard_dao);
                                }
                                break;
                            case "get-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command get-vm");
                                request_message = dashboard_dao.getVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if (request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.getVm(request_message);
                                } else {
                                    response_message.setStatus(FAILED);
                                }
                                break;
                            case "new-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command new-vm");
                                request_message = dashboard_dao.newVM(request_message);

                                // If customer does not have a vm with the same VM name then proceed processing
                                if (request_message.getStatus_request() != DUPLICATE) {
                                    response_message = provisioning_server.newVm(request_message);

                                    // if response message of provisioning server is succesfull store vm data in db
                                    if (response_message.getStatus() == SUCCEED) {
                                        response_message = dashboard_dao.CreateNewVM(request_message);
                                    }
                                } else {
                                    response_message.setStatus(DUPLICATE);
                                }
                                break;
                            case "start-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command start-vm");
                                request_message = dashboard_dao.startVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if (request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.startVm(request_message);
                                } else {
                                    response_message.setStatus(FAILED);
                                }

                                break;
                            case "stop-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command start-vm");
                                request_message = dashboard_dao.stopVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if (request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.stopVm(request_message);
                                } else {
                                    response_message.setStatus(FAILED);
                                }
                                break;

                            case "delete-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command delete-vm");
                                request_message = dashboard_dao.deleteVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if (request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.deleteVm(request_message);
                                } else {
                                    response_message.setStatus(FAILED);
                                }

                                if (response_message.getStatus().equals(SUCCEED)) {
                                    // if response of provisioning server is positive then mark vm as deleted in db
                                    response_message = dashboard_dao.updateDeleteVM(request_message);
                                }
                                break;

                            case "backup-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command backup-vm");
                                request_message = dashboard_dao.backupVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if (request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.backupVm(request_message);
                                } else {
                                    response_message.setStatus(FAILED);
                                }
                                break;

                            case "restore-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command restore-vm");
                                request_message = dashboard_dao.restoreVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if (request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.restoreVM(request_message);
                                } else {
                                    response_message.setStatus(FAILED);
                                }
                                break;
                            case "modify-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command modify-vm");
                                request_message = dashboard_dao.modifyVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if (request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.modifyVm(request_message);
                                } else {
                                    response_message.setStatus(FAILED);
                                }
                                break;

                            case "employee-data":
                                System.out.println("Dashboard controller: command employee-data");
                                response_message = dashboard_dao.employeeData(request_message);
                                break;
                            case "company-data":
                                System.out.println("Dashboard controller: command company-data");
                                response_message = dashboard_dao.companyData(request_message);
                                break;

                            case "get-hypervisors":
                                // Set payload to array
                                payload_is_array = true;

                                System.out.println("Dashboard controller: command get-hypervisor");
                                response_message = dashboard_dao.getHypervisorData(request_message);
                                break;

                            case "set-hypervisor":
                                System.out.println("Dashboard controller: command set-hypervisor");
                                response_message = dashboard_dao.setHypervisorData(request_message);
                                break;
                            case "get-templates":
                                // Set payload to array
                                payload_is_array = true;

                                System.out.println("Dashboard controller: command get_templates");
                                response_message = dashboard_dao.getTemplateData(request_message);
                                break;

                            case "get-service-levels":
                                // Set payload to array
                                payload_is_array = true;

                                System.out.println("Dashboard controller: command service_levels");
                                response_message = dashboard_dao.getServiceLevelData(request_message);
                                break;
                            case "get-bill":
                                System.out.println("Dashboard controller: command get_bill");

                                if(request_message.getParams().equals("DATES")) {
                                    getBillingDates(request_message, response_message, dashboard_dao);
                                }else{
                                    Invoice invoice = getMonthBill(request_message, dashboard_dao);

                                    // call sendInvoice api to generate a pdf
                                    Send_Invoice send_invoice = new Send_Invoice();
                                    byte[] pdf = send_invoice.sendRequest(invoice);

                                    // adjust response entity
                                    response.reset();
                                    response.setContentType("application/pdf");
                                    response.setHeader("Content-Disposition", "inline; filename=bill.pdf");

                                    // write output stream to front-end
                                    ServletOutputStream out = response.getOutputStream();
                                    response.setContentLength(pdf.length);
                                    out.write(pdf);
                                    out.flush();
                                    out.close();
                                }
                                break;
                        }

                        // Send Response Message
                        try (PrintWriter out = response.getWriter()) {
                            if(payload_is_array == true){

                                out.println(response_message.toJsonStringWithArray());
                                System.out.println("Controller: Json response message (json array): " + response_message.toJsonStringWithArray());    // debug comment
                            }else{
                                out.println(response_message.toJsonString());
                                System.out.println("Controller: Json response message: " + response_message.toJsonString());    // debug comment
                            }
                        }
                    }
                } catch (ParseException | SQLException | ClassNotFoundException e) {
                    System.out.println("Controller: received data is not of JSON format");    // debug comment
                    e.printStackTrace();
                }

            } else {
                response.sendError(400, REQUEST_INVALID_PARMS);
            }

        } else {
            // Send no session response message
            response_message.setStatus(NOSESSION);

            System.out.println("User whitout a valid session");    // debug comment
            try (PrintWriter out = response.getWriter()) {
                System.out.println("Controller: Json response message: " + response_message.toJsonString());    // debug comment

                if(payload_is_array == true){
                    out.println(response_message.toJsonStringWithArray());
                    System.out.println("Controller: Json response message (json array): " + response_message.toJsonStringWithArray());    // debug comment
                }else{
                    out.println(response_message.toJsonString());
                    System.out.println("Controller: Json response message: " + response_message.toJsonString());    // debug comment
                }

            }
        }


    }


    // methods returns a Request object filled with received json data
    private Request fillRequestData(JSONObject jsonObject) {
        Request request_message = new Request();

        if (jsonObject.containsKey("command")) {
            request_message.setCommand(jsonObject.get("command").toString());
        }

        if (jsonObject.containsKey("params")) {
            request_message.setParams(jsonObject.get("params").toString());
        }

        if (jsonObject.containsKey("hypervisor")) {
            request_message.setHypervisor(jsonObject.get("hypervisor").toString());
        }

        if (jsonObject.containsKey("vm")) {
            request_message.setVm(jsonObject.get("vm").toString());
        }

        if(jsonObject.containsKey("service_level")){
            request_message.setService_level(jsonObject.get("service_level").toString());
        }
        return request_message;
    }


    // Method adds additional data from database to received data from provisioning server
    private Response ListVmData(Response response, String company_id, Dashboard_Dao dashboard_dao) {
        JSONArray jsonArray = new JSONArray();

        // parse json payload string
        try {
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(response.getPayload());


            // get all vm's off the customer
            List<Virtual_Machine> virtual_machines = dashboard_dao.dbReadVirtualMachines(company_id);

            // loop through the Virtual machines
            for (Virtual_Machine virtual_machine : virtual_machines) {
                if (jsonObject.containsKey(virtual_machine.getVm_id())) {
                    virtual_machine.setState(jsonObject.get(virtual_machine.getVm_id()).toString());
                    jsonArray.add(virtual_machine.toJsonObject());
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(FAILED);
            response.setPayload("Dashboard dao: ListVmData() json format error");
        }


        // Set json array payload
        response.setPayload_array(jsonArray);

        return response;
    }

    // Methods returns the billing all the billing dates of the customer. Billing date is set to the 27th of the month
    private Response getBillingDates(Request request_message, Response response_message, Dashboard_Dao dashboard_dao) {

        // List for storing the dates
        List<Calendar> billing_dates = new ArrayList<Calendar>();

        // Get employee data from db
        Employee employee = dashboard_dao.dbReadEmployee(request_message.getUsername());

        // Get company data from db
        Company company = dashboard_dao.dbReadCompany(employee.getCompany_id());

        // set date customer sign in date
        Calendar signin_date = Calendar.getInstance();
        signin_date.setTime(company.getCreate_date());

        // get last billing date
        Calendar actual_date = Calendar.getInstance();

        // check which day of the month it is. If actual day is before billin date the last bill date is the previous month.
        // If it is the first month of the year the billing date is set to the last month of the previous year
        if (actual_date.get(Calendar.DAY_OF_MONTH) < 27 && actual_date.get(Calendar.MONTH) == 0) {
            actual_date.set((actual_date.get(Calendar.YEAR) - 1), 11, 27);
        } else if (actual_date.get(Calendar.DAY_OF_MONTH) < 27) {
            actual_date.set(actual_date.get(Calendar.YEAR), actual_date.get(Calendar.MONTH), actual_date.get(Calendar.MONTH) - 1);
        }

        // temp variables used for counting
        int count_year = signin_date.get(Calendar.YEAR);
        int count_month;

        // iff billing day passed go to the next month
        if (signin_date.get(Calendar.DAY_OF_MONTH) > 27) {
            count_month = signin_date.get(Calendar.MONTH) + 1;
        } else {
            count_month = signin_date.get(Calendar.MONTH);
        }
        // get billing dates of previous years
        while (count_year < actual_date.get(Calendar.YEAR)) {

            while (count_month <= 11) {

                // Set the date billing date
                Calendar bill_date = Calendar.getInstance();
                bill_date.set(count_year, count_month, 27);

                // add to the list
                billing_dates.add(bill_date);

                // increment month
                count_month = count_month + 1;
            }

            // increment year and reset the month
            count_year = count_year + 1;
            count_month = 0;
        }

        // get billing dates of current year

        // check if it is the same year
        if (actual_date.get(Calendar.YEAR) == count_year) {
            while (count_month <= actual_date.get(Calendar.MONTH)) {

                // Set the date billing date
                Calendar bill_date = Calendar.getInstance();
                bill_date.set(count_year, count_month, 27);

                // add to the list
                billing_dates.add(bill_date);

                // increment month
                count_month = count_month + 1;
            }
        }

        // Create payload
        StringBuilder payload = new StringBuilder();
        for (Calendar bill_date : billing_dates) {

            Date presentation_date = new Date(bill_date.getTimeInMillis());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("billing_date", presentation_date);
            payload.append(jsonObject.toJSONString());
        }
        response_message.setPayload(payload.toString());
        response_message.setStatus(SUCCEED);

        return response_message;
    }

    // Method return a bill of the required month
    private Invoice getMonthBill(Request request_message, Dashboard_Dao dashboard_dao) {
        Invoice invoice = new Invoice();

        // get the required date from the request
        try {
            // converse the received date to usable format
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = format.parse(request_message.getParams());

            // define the time frame of the bill
            Calendar last_bill_day = Calendar.getInstance();
            last_bill_day.setTimeInMillis(date.getTime());

            // determine the first day of the bill. if last bill day month is the first month of the year, the year off the first day will be the previous year
            Calendar first_bill_day = Calendar.getInstance();
            if (last_bill_day.get(Calendar.MONTH) == 0) {
                first_bill_day.set(last_bill_day.get(Calendar.YEAR) - 1, 11, 28);
            } else {
                first_bill_day.set(last_bill_day.get(Calendar.YEAR), last_bill_day.get(Calendar.MONTH) - 1, 28);
            }

            // temp calculation variables
            Long vm_end_date;
            Long vm_begin_date;
            float factor;

            // get all required data from database to calculate the bill

            // get all custumer info
            Employee employee = dashboard_dao.dbReadEmployee(request_message.getUsername());

            // get all company info
            Company company = dashboard_dao.dbReadCompany(employee.getCompany_id());

            // get all vm's off the customer
            List<Virtual_Machine> virtual_machines = dashboard_dao.dbReadVirtualMachines(employee.getCompany_id());

            // get all service level from de db
            List<Service_Level> service_levels = dashboard_dao.dbReadServiceLevels();

            //get all templates from db
            List<Template> templates = dashboard_dao.dbReadTemplates();

            // Create billing list
            List<Bill> billings = new ArrayList<Bill>();

            // loop through the Virtual machines
            for (Virtual_Machine virtual_machine : virtual_machines) {

                Bill bill = new Bill();
                // init variabeles
                 factor = 0.0f;

                // loop through the service levels and add the commision to the bill
                for (Service_Level service_level : service_levels) {
                    if (virtual_machine.getService_level().equals(service_level.getName())) {
                        bill.setCommision(service_level.getCommision());
                        bill.setService_level(service_level.getName());
                        factor = (float) bill.getCommision() / 100.0f + 1.0f;
                        break;
                    }
                }

                // loop through templates machines and add data to the bill
                for (Template template : templates) {
                    if (virtual_machine.getTemplate_id().equals(template.getTemplate_id())) {

                        // add virtual machine and template information to bill
                        bill.setOperating_system(template.getOperating_system());
                        bill.setPrice(template.getPrice());
                        bill.setVm_name(virtual_machine.getVm_name());
                        break;
                    }
                }

                //<---------------- Create Bill --------------->\\

                // get create date of virtual machine
                vm_begin_date = virtual_machine.getCreate_date().getTime();

                // Get the end date off the virtual machine. If there is no end date available, the date of today is used
                if (virtual_machine.getDelete_date() == null) {
                    date = new java.util.Date();
                    vm_end_date = date.getTime();
                } else {
                    vm_end_date = virtual_machine.getDelete_date().getTime();
                }

                // check hte billing period of the vm. There are four possibilities
                // 1. start before begin and end after end
                // 2. start before begin and end in period
                // 3. start after begin and end in period
                // 3. start after begin and end after end

                // two date instances used for storing the date period on the bill. temperoly value is used of initializing
                Date begin_date = new java.sql.Date(date.getTime());
                Date end_date = new java.sql.Date(date.getTime());

                if (vm_begin_date <= first_bill_day.getTimeInMillis() && vm_end_date >= last_bill_day.getTimeInMillis()) {
                    // calculate costs
                    bill.setMonth_days(TimeUnit.MILLISECONDS.toDays(last_bill_day.getTimeInMillis() - first_bill_day.getTimeInMillis()));
                    bill.setMonth_costs_vm(bill.getMonth_days() * bill.getPrice() * factor);

                    // billing period of the vm
                    begin_date = new java.sql.Date(first_bill_day.getTimeInMillis());
                    end_date = new java.sql.Date(last_bill_day.getTimeInMillis());

                }// check if vm begin date was before first billing date and vm end day was before last billing date
                else if (vm_begin_date <= first_bill_day.getTimeInMillis() && vm_end_date <= last_bill_day.getTimeInMillis()) {
                    bill.setMonth_days(TimeUnit.MILLISECONDS.toDays(vm_end_date - first_bill_day.getTimeInMillis()));
                    bill.setMonth_costs_vm(bill.getMonth_days() * bill.getPrice() * factor);

                    // billing period of the vm
                    begin_date = new java.sql.Date(first_bill_day.getTimeInMillis());
                    end_date = new java.sql.Date(vm_end_date);

                }// check if vm begin date was after first billing date and vm end day was before last billing date
                else if (vm_begin_date > first_bill_day.getTimeInMillis() && vm_end_date <= last_bill_day.getTimeInMillis()) {
                    bill.setMonth_days(TimeUnit.MILLISECONDS.toDays(vm_end_date - vm_begin_date));
                    bill.setMonth_costs_vm(bill.getMonth_days() * bill.getPrice() * factor);

                    // billing period of the vm
                    begin_date = new java.sql.Date(vm_begin_date);
                    end_date = new java.sql.Date(vm_end_date);

                }// check if vm begin date was after first billing date and vm end day was before last billing date
                else if (vm_begin_date > first_bill_day.getTimeInMillis() && vm_end_date > last_bill_day.getTimeInMillis()) {
                    bill.setMonth_days(TimeUnit.MILLISECONDS.toDays(last_bill_day.getTimeInMillis() - vm_begin_date));
                    bill.setMonth_costs_vm(bill.getMonth_days() * bill.getPrice() * factor);

                    // billing period of the vm
                    begin_date = new java.sql.Date(vm_begin_date);
                    end_date = new java.sql.Date(last_bill_day.getTimeInMillis());
                }

                // check if there is a bill for the virtual machine, else check next vm
                if(bill.getMonth_days() > 0.0){
                    // add the billing period to the bill
                    bill.setBilling_period_date(begin_date + " - " + end_date);
                    billings.add(bill);
                }
            }

            // Set invoice information
            Date start_bill_period = new Date(first_bill_day.getTimeInMillis());
            Date end_bill_period = new Date(last_bill_day.getTimeInMillis());
            invoice.setBill_period(start_bill_period + " - " + end_bill_period);
            invoice.setInvoice_date(end_bill_period.toString());
            invoice.setCompany(company);
            invoice.setBills(billings);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return invoice;
    }


}
