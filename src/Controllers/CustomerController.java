package Controllers;

import Data_Access_Objects.Customer_Dao;
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

public class CustomerController extends HttpServlet {
        private final String REQUEST_INVALID_PARMS = "The request contains invalid parameters.";
        private final String DUPLICATE = "DUPLICATE";
        private final String VALID = "VALID";
        private final String MATCH = "MATCH";


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Customer controller: HTTP POST Request");    // debug comment

        // declarations
        JSONObject jsonObjectRequest;
        Customer_Dao customer_dao = null;
        String jsonStringRequest;
        String response_msg = null;

        // new instance of customer data access object
        try {
            customer_dao = new Customer_Dao();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        // Read POST data en write it in a String
        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            jsonStringRequest = br.readLine();
            System.out.println("Customer controller: following Json String received: " + jsonStringRequest);    // debug comment
        }

        if (jsonStringRequest != null){
            try {
                // Process the data
                jsonObjectRequest = (JSONObject) JSONValue.parseWithException(jsonStringRequest);

                // check which action is chosen
                if ((jsonObjectRequest.get("action").toString()).equals("insert")) {
                    response_msg = customer_dao.insertCustomer(jsonObjectRequest);
                    System.out.println("Controller: insert action");    // debug comment
                } else if ((jsonObjectRequest.get("action").toString()).equals("login")) {
                    response_msg = customer_dao.loginCustomer(jsonObjectRequest);
                    System.out.println("Controller: login action");    // debug comment
                }

                // If login successful set session
                if (response_msg.equals(MATCH)){
                    HttpSession session = request.getSession();
                    session.setAttribute("username", jsonObjectRequest.get("username").toString());
                }

                // Build response message
                JSONObject jsonObjectResponse = new JSONObject();
                jsonObjectResponse.put("status", response_msg);

                // Send Response Message
                try (PrintWriter out = response.getWriter()) {
                    System.out.println("Controller: Json response message: " + jsonObjectResponse.toJSONString());    // debug comment
                    out.println(jsonObjectResponse.toJSONString());
                }

            } catch (ParseException e) {
            System.out.println("Controller: received data is not of JSON format");    // debug comment
            e.printStackTrace();
            }

        }else {
            response.sendError(400,REQUEST_INVALID_PARMS);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        System.out.println("Controller: HTTP GET Request");    // debug comment
        Customer_Dao customer_dao = null;

        // Build response message
        JSONObject jsonObjectResponse = new JSONObject();

        String action = request.getParameter("action");
        String company_name = request.getParameter("company_name");
        String validate = DUPLICATE;


        if (action != null){
            switch (action) {
                case "check_name":

                    // new instance of customer data access object
                    try {
                        customer_dao = new Customer_Dao();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                        // get dashboard data
                        validate = customer_dao.validateInsert(company_name);
                    break;
            }

            // If Data is valid generate a username
            if(validate.equals(VALID)){
                String username = company_name.replaceAll("\\s", "").toLowerCase() + "." + "administrator";
                jsonObjectResponse.put("username", username);
            }

            jsonObjectResponse.put("status", validate);

            try (PrintWriter out = response.getWriter()) {
                System.out.println("Controller: Json response message: " + jsonObjectResponse.toJSONString());    // debug comment
                out.println(jsonObjectResponse.toJSONString());
            }
        }else {
            response.sendError(400, REQUEST_INVALID_PARMS);
        }

    }



}
