package Controllers;

import Data_Access_Objects.Dashboard_Dao;
import Provisioning_API.Provisioning_Server;
import Models.Request;
import Models.Response;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;

public class DashboardController extends HttpServlet {

    private final String REQUEST_INVALID_PARMS = "The request contains invalid parameters.";
    private final String NOSESSION = "NOSESSION";
    private final String DUPLICATE = "DUPLICATE";
    private final String FAILED = "FAILED";
    private final String SUCCEED = "SUCCEED";

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
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command list-vms");
                                request_message = dashboard_dao.listVMs(request_message);
                                response_message = provisioning_server.listVms(request_message);
                                break;
                            case "get-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command get-vm");
                                request_message = dashboard_dao.getVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if(request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.getVm(request_message);
                                }else{
                                    response_message.setStatus(FAILED);
                                }
                                break;
                            case "new-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command new-vm");
                                request_message = dashboard_dao.newVM(request_message);

                                // If customer does not have a vm with the same VM name then proceed processing
                                if(request_message.getStatus_request() != DUPLICATE){
                                    response_message = provisioning_server.newVm(request_message);

                                    // if response message of provisioning server is succesfull store vm data in db
                                    if(response_message.getStatus() == SUCCEED){
                                        response_message = dashboard_dao.CreateNewVM(request_message);
                                    }
                                }else{
                                    response_message.setStatus(DUPLICATE);
                                }
                                break;
                            case "start-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command start-vm");
                                request_message = dashboard_dao.startVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if(request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.startVm(request_message);
                                }else{
                                    response_message.setStatus(FAILED);
                                }

                                break;
                            case "stop-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command start-vm");
                                request_message = dashboard_dao.stopVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if(request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.stopVm(request_message);
                                }else{
                                    response_message.setStatus(FAILED);
                                }
                                break;

                            case "delete-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command delete-vm");
                                request_message = dashboard_dao.deleteVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if(request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.deleteVm(request_message);
                                }else{
                                    response_message.setStatus(FAILED);
                                }

                                if (response_message.getStatus().equals(SUCCEED)){
                                    // if response of provisioning server is positive then mark vm as deleted in db
                                    response_message = dashboard_dao.updateDeleteVM(request_message);
                                }
                                break;

                            case "backup-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command backup-vm");
                                request_message = dashboard_dao.backupVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if(request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.backupVm(request_message);
                                }else{
                                    response_message.setStatus(FAILED);
                                }
                                break;

                            case "restore-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command restore-vm");
                                request_message = dashboard_dao.restoreVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if(request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.restoreVM(request_message);
                                }else{
                                    response_message.setStatus(FAILED);
                                }
                                break;
                            case "modify-vm":
                                // get data from db for request to provisioning server
                                System.out.println("Dashboard controller: command modify-vm");
                                request_message = dashboard_dao.modifyVM(request_message);

                                // proceed to provisioning server if the requested data is found in db
                                if(request_message.getStatus_request() != FAILED) {
                                    response_message = provisioning_server.modifyVm(request_message);
                                }else{
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
                                System.out.println("Dashboard controller: command get-hypervisor");
                                response_message = dashboard_dao.getHypervisorData(request_message);
                                break;

                            case "set-hypervisor":
                                System.out.println("Dashboard controller: command set-hypervisor");
                                response_message = dashboard_dao.setHypervisorData(request_message);
                                break;
                            case "get-templates":
                                System.out.println("Dashboard controller: command get_templates");
                                response_message = dashboard_dao.getTemplateData(request_message);
                                break;

                        }

                        // Send Response Message
                        try (PrintWriter out = response.getWriter()) {
                            System.out.println("Controller: Json response message: " + response_message.toJsonString());    // debug comment
                            out.println(response_message.toJsonString());
                        }
                    }
                } catch (ParseException | SQLException |ClassNotFoundException  e) {
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
                out.println(response_message.toJsonString());
            }
        }


    }


    // methods returns a Request object filled with received json data
    private Request fillRequestData(JSONObject jsonObject){
        Request request_message = new Request();

        if(jsonObject.containsKey("command")){
            request_message.setCommand(jsonObject.get("command").toString());
        }

        if(jsonObject.containsKey("params")){
            request_message.setParams(jsonObject.get("params").toString());
        }

        if(jsonObject.containsKey("hypervisor")){
            request_message.setHypervisor(jsonObject.get("hypervisor").toString());
        }

        if(jsonObject.containsKey("vm")){
            request_message.setVm(jsonObject.get("vm").toString());
        }
        return request_message;
    }


}
