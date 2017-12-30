package Data_Access_Objects;

import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.UUID;


public class Customer_Dao {

    // State messages
    private final String FAILED = "FAILED";
    private final String SUCCEED = "SUCCEED";
    private final String DUPLICATE = "DUPLICATE";
    private final String VALID = "VALID";
    private final String MATCH = "MATCH";
    private final String NOMATCH = "NOMATCH";

    private Connection connection;
    private DBConnectionManager dbManager;

    // constructor
    public Customer_Dao()throws SQLException, ClassNotFoundException {
        // open database connection
        dbManager = new DBConnectionManager();
        connection = dbManager.getConnection();
    }

    // method controls insert query
    public String insertCustomer(JSONObject customer){

        String UUID = null;
        String password = null;

        // if the data is valid, insert the data in db. Else return message Duplicate
        if(validateInsert(customer.get("company_name").toString()).equals(VALID)){
            System.out.println("Customer_Dao: insert Data is valid");    // debug comment

            // call method that creates a UUID for the db table company
            UUID = createUUID("company");

            // create a hash off the password
            password = encryptPassword(customer.get("password").toString());

            try {
                // prepare db statement for table company
                PreparedStatement preparedStatement = connection
                        .prepareStatement("insert into company(company_id, company_name, zip_code, city, address) values (?, ?, ?, ?, ?)");

                // set db set and insert data in table company
                preparedStatement.setString(1, UUID);
                preparedStatement.setString(2, customer.get("company_name").toString());
                preparedStatement.setString(3, customer.get("zip_code").toString());
                preparedStatement.setString(4, customer.get("city").toString());
                preparedStatement.setString(5, customer.get("address").toString());
                preparedStatement.executeUpdate();

                // prepare db statement for table company
                preparedStatement = connection
                        .prepareStatement("insert into employee(username, role, firstname, lastname, email, company_id, password) values (?, ?, ?, ?, ?, ?,?)");

                // First user is an administrator
                String role = "administrator";

                // set db set and insert data in table company
                preparedStatement.setString(1, customer.get("company_name").toString().replaceAll("\\s", "").toLowerCase() + "." + "administrator");
                preparedStatement.setString(2, role);
                preparedStatement.setString(3, customer.get("firstname").toString());
                preparedStatement.setString(4, customer.get("lastname").toString());
                preparedStatement.setString(5, customer.get("email").toString());
                preparedStatement.setString(6, UUID);
                preparedStatement.setString(7, password);
                preparedStatement.executeUpdate();

                System.out.println("Customer_Dao: insert query succeeded");    // debug comment
                return SUCCEED;

            } catch (SQLException e) {

                System.out.println("Customer_Dao: insert query failed");    // debug comment
                e.printStackTrace();
                return FAILED;
            }
            // if data is not valid then return message DUPLICATE
        }else{
            System.out.println("Customer_Dao: insert data not valid");    // debug comment
            return DUPLICATE;
        }


    }

    // INSERT - Methods creates a UUID for a specific db table
    private String createUUID(String db_table){

        UUID uuid = UUID.randomUUID();
        String UUID = uuid.toString();

        System.out.println("Customer_Dao: Insert data new generated UUID: " + UUID);    // debug comment
        return UUID;
    }


    // Method adds a string (salting) to password and creates a hash.
    private String encryptPassword(String password){
        // add salting text to password
        password = password + "Emeri&Pum";
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedhash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(encodedhash);
    }

    // methods converts bytes to hex and is used by the ecrypt password method
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    // INSERT - method checks if data is duplicate.
    public String validateInsert(String company_name){
        String return_value = DUPLICATE;

        try {
            // prepare db statement to check if company name already exist
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM company WHERE company_name =? ");

            preparedStatement.setString(1, company_name);
            ResultSet rs = preparedStatement.executeQuery();

            // check if db returned data
            if (rs.next()) {
                System.out.println("Customer_Dao: Company with same name found in db");    // debug comment
            } else {
                return_value = VALID;
            }

        } catch (SQLException e) {
        System.out.println("Customer_Dao: Validation query failed");    // debug comment
        e.printStackTrace();
    }

        return return_value;
    }

    // LOGIN - method controls login of customer
    public String loginCustomer(JSONObject customer){
        String return_value = NOMATCH;
        String password = encryptPassword(customer.get("password").toString());

        try {
            // prepare db statement to check if company name already exist
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT password FROM employee WHERE username =? ");

            preparedStatement.setString(1, customer.get("username").toString());
            ResultSet rs = preparedStatement.executeQuery();

            // check if db returned data
            if (rs.next()) {
                System.out.println("Customer_Dao: username found in database");    // debug comment
                if(password.equals(rs.getString("password"))){
                    System.out.println("Customer_Dao: username and password match with user input");    // debug comment
                    return_value = MATCH;
                }else{
                    return_value = NOMATCH;
                }
            } else {
                return_value = NOMATCH;
            }

        } catch (SQLException e) {
            System.out.println("Customer_Dao: Login query failed");    // debug comment
            e.printStackTrace();
        }

        return return_value;
    }




}
