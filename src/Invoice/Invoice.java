package Invoice;

import Models.Request;
import Models.Response;
import sun.misc.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Invoice {

    private static String URL = "http://localhost:8080/pdf-getInvoice";


    /////////////////////////////////////**********  HTTP communication  **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public byte[] sendRequest(Response response) {
        byte[] bytes = null;
        URL obj;
        HttpURLConnection con;

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
            wr.writeBytes(response.getPayload());
            int responseCode;

            // clear the output stream
            wr.flush();
            wr.close();
            responseCode = con.getResponseCode();

            // Print parameters HTTP POST request
            System.out.println("nSending 'POST' request to URL : " + URL);
            System.out.println("Post Data : " + response.toJsonString());
            System.out.println("Response Code : " + responseCode);

            // check the response message code
            if (responseCode == 200) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream is;

                try {
                    is = con.getInputStream();
                    byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
                    int n;

                    while ( (n = is.read(byteChunk)) > 0 ) {
                        baos.write(byteChunk, 0, n);
                    }
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace ();
                }

                bytes = baos.toByteArray();
            }else{
                // error handling for response message error codes
                System.out.println("Respond error received: " + responseCode);
            }
        } catch (IOException e) {
            System.out.println("HTTP request to provisioning server failed");
            e.printStackTrace();

        }

        return bytes;
    }
}
