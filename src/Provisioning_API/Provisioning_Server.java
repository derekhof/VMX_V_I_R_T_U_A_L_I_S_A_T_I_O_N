package Provisioning_API;

import Models.Request;
import Models.Response;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Provisioning_Server {

    // Constant variabeles
    private final String URL = "http://prov.vmx.local:8080/simulatie";       // URL of the provisioning server
    private final String FAILED = "FAILED";
    private final String SUCCEED = "SUCCEED";
    private Response response = new Response();

    /////////////////////////////////**********  presentation commando's  **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Method returns a list of all the vm's of a specific customer
    public Response listVms(Request request){

        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: new list vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }

    // Method returns a specific vm of a customer
    public Response getVm(Request request){

        System.out.println("Provisioning server: following request is send: " + request.toJsonString());
        // print action
        System.out.println("Provisioning server: new get vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }



    /////////////////////////////////**********  VM control commando's   **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Method powers-ups a specific vm
    public Response startVm(Request request){
        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: start vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }

    // Method Stops a specific vm
    public Response stopVm(Request request) {
        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: stop get vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }


    ////////////////////////////////**********  VM maintenance commando's   **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Method creates a new vm
    public Response newVm(Request request){
        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: create new vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }

    public Response deleteVm(Request request){
        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: delete vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;

    }

    // Method creates a backup of a specific vm
    public Response backupVm(Request request){
        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: backup vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }


    //Method restores a specific vm
    public Response restoreVM(Request request){
        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: restore vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }


    // Method modifies a specific vm
    public Response modifyVm(Request request){
        System.out.println("Provisioning server: following request is send: " + request.toJsonString());

        // print action
        System.out.println("Provisioning server: modfiy vm action");
        // send request to provisioning server
        String response_payload = sendRequest(request);

        // check if the response message is valid
        response = validateResponseMessage(response_payload);

        return response;
    }


    /////////////////////////////////////**********  HTTP communication  **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public String sendRequest(Request request) {

        URL obj;
        HttpURLConnection con;
        String responseMessage = null;

        try {
            obj = new URL(URL);
            con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = null;


            // create output stream.
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(request.toJsonString());
            int responseCode;

            // clear the output stream
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            wr.toString();
            BufferedReader in;

            // Print parameters HTTP POST request
            System.out.println("nSending 'POST' request to URL : " + URL);
            System.out.println("Post Data : " + request.toJsonString());
            System.out.println("Response Code : " + responseCode);

            // check the response message code
            if (responseCode == 200) {

                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String output;
                StringBuffer response = new StringBuffer();

                // schrijf inhoud buffered reader in een string
                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
                // sluit buffered reader
                in.close();

                // response message
                responseMessage = response.toString();

                //printing result from response
                System.out.println(response.toString());

            }else{
                // error handling for response message error codes

                System.out.println("Respond error received: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println("HTTP request to provisioning server failed");
            e.printStackTrace();

        }

        return responseMessage;
    }


    private Response validateResponseMessage(String response_message){

        // parse received json string
        try {
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(response_message);

            if(jsonObject.containsKey("Error")){
                response.setStatus(FAILED);
                response.setPayload(jsonObject.get("Error").toString());
            }else{
                response.setStatus(SUCCEED);
                response.setPayload(response_message);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(FAILED);
            response.setPayload("Communication error with provisioning server");
        }


        return response;
    }








}
