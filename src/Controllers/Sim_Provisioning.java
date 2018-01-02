package Controllers;

import Data_Access_Objects.Customer_Dao;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;

public class Sim_Provisioning extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        System.out.println("Simulatie: HTTP POST Request");    // debug comment

        // declarations
        JSONObject jsonObjectRequest;
        JSONObject jsonObjectResponse = new JSONObject();
        String jsonStringRequest;


        // Read POST data en write it in a String
        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            jsonStringRequest = br.readLine();
            System.out.println("Simulatie: following Json String received: " + jsonStringRequest);    // debug comment
        }


        if (jsonStringRequest != null) {
            try {
                // Process the data
                jsonObjectRequest = (JSONObject) JSONValue.parseWithException(jsonStringRequest);

                // check which action is chosen
                if ((jsonObjectRequest.get("command").toString()).equals("list-vms")) {

                    jsonObjectResponse.put("00a68d9e-439b-4019-88e9-2aabc7692536", "off");
                    jsonObjectResponse.put("1ea18ca0-2adb-4676-a2b1-c7cc600ea961", "off");
                    jsonObjectResponse.put("50ef0644-e32b-4b54-adef-6ac4a18e8249", "off");
                    jsonObjectResponse.put("538ee067-a1fd-44fd-8845-e3bc2f622f4d", "on");
                    jsonObjectResponse.put("bbda1fdf-7661-48a0-a10c-6e7ef5da68df", "on");
                    jsonObjectResponse.put("c4e5cc7e-453d-465f-a6de-a267d129b0ec", "on");

                    System.out.println("simulatie: ");    // debug comment
                } else if ((jsonObjectRequest.get("command").toString()).equals("start-vm")) {

                    jsonObjectResponse.put("result", "started");

                    System.out.println("simulatie: ");    // debug comment
                } else if ((jsonObjectRequest.get("command").toString()).equals("stop-vm")) {
                    jsonObjectResponse.put("result", "stopped");

                    System.out.println("simulatie: ");    // debug comment
                } else if ((jsonObjectRequest.get("command").toString()).equals("new-vm")) {

                    jsonObjectResponse.put("sne1", "has booted");

                    System.out.println("simulatie: ");    // debug comment
                } else if ((jsonObjectRequest.get("command").toString()).equals("delete-vm")) {

                    jsonObjectResponse.put("result", "R.I.P. sne1");
                    System.out.println("simulatie: ");    // debug comment
                }

            } catch (ParseException e) {
                System.out.println("Controller: received data is not of JSON format");    // debug comment
                e.printStackTrace();
            }

            // Send Response Message
            try (PrintWriter out = response.getWriter()) {
                System.out.println("Simulatie: Json response message: " + jsonObjectResponse.toJSONString());    // debug comment
                out.println(jsonObjectResponse.toJSONString());
            }

        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
