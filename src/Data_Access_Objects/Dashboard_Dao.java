package Data_Access_Objects;

import Models.*;
import Models.Request;
import Models.Response;
import org.json.simple.JSONArray;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Dashboard_Dao {
    // State messages
    private final String FAILED = "FAILED";
    private final String SUCCEED = "SUCCEED";
    private final String CUSTOMER = "CUSTOMER";
    private final String DELETE =  "DELETE";
    private final String DUPLICATE = "DUPLICATE";
    private Response response = new Response();
    private Connection connection;
    private DBConnectionManager dbManager;

    // constructor
    public Dashboard_Dao() throws SQLException, ClassNotFoundException {
        // open database connection
        dbManager = new DBConnectionManager();
        connection = dbManager.getConnection();
    }

    /////////////////////////////////**********  Data Response objects   **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Method returns a response object with employee data
    public Response employeeData(Request request) {
        // Get data of employee
        Employee employee = dbReadEmployee(request.getUsername());

        // Fill response object if there is valid data
        if (employee.getUsername() != "") {
            response.setStatus(SUCCEED);
            response.setPayload(employee.toJsonString());
        } else {
            response.setStatus(FAILED);
        }
        return response;
    }

    // Method returns a response object with employee data
    public Response companyData(Request request) {

        // Get Employee data
        Employee employee = dbReadEmployee(request.getUsername());

        // Get company data if employee data available
        if (employee.getCompany_id() != "null") {
            // Get data of employee
            Company company = dbReadCompany(employee.getCompany_id());

            if (company.getCompany_id() != "") {
                response.setStatus(SUCCEED);
                response.setPayload(company.toJsonString());
            } else {
                response.setStatus(FAILED);
            }
        } else {
            response.setStatus(FAILED);
        }
        return response;
    }

    // Method returns requested data of the Hypervisors
    public Response getHypervisorData(Request request) {
        // Get customer data
        Employee employee = dbReadEmployee(request.getUsername());
        System.out.println("Dashboard controller: get employee data from db");

        // get the hypervisor data from db, depending of the request params
        List<Hypervisor> hypervisors = dbReadHypervisor(request.getParams(), employee.getCompany_id());

        // create a payload for the response
        JSONArray jsonArray = new JSONArray();

        // loop through the hypervisors
        for (Hypervisor hypervisor : hypervisors) {
            // add the hypervisor to the response payload
            jsonArray.add(hypervisor.toJsonObject());
        }
        response.setStatus(SUCCEED);
        response.setPayload_array(jsonArray);

        return response;
    }

    // Method connects a company to a hypervisor
    public Response setHypervisorData(Request request){
        // If it is a delete action replace company_id with null, otherwise add company_id to hypervisor
        if(request.getParams().equals("ADD")){
            // Get customer data
            System.out.println(request.getParams());
            Employee employee = dbReadEmployee(request.getUsername());
            request.setCompany_id(employee.getCompany_id());
        }

        System.out.println(request.toJsonString());

        // insert company_id in the specific hypervisor record
        response.setStatus(dbUpdateCompanyHypervisor(request.getCompany_id(), request.getHypervisor()));

        return response;
    }

    // Method returns vm templates from db
    public Response getTemplateData(Request request){

        // get the template data from db, depending of the request params
        List<Template> templates = dbReadTemplates();

        // create a payload for the response
        JSONArray jsonArray = new JSONArray();

        // loop through the hypervisors
        for (Template template : templates) {
            // add the hypervisor to the response payload
            jsonArray.add(template.toJsonObject());
        }

        response.setStatus(SUCCEED);
        response.setPayload_array(jsonArray);

        return response;
    }

    // Method returns vm service levels from db
    public Response getServiceLevelData(Request request){

        // get the template data from db, depending of the request params
        List<Service_Level> service_levels = dbReadServiceLevels();

        // create a payload for the response
        JSONArray jsonArray = new JSONArray();

        // loop through the hypervisors
        for (Service_Level service_level : service_levels) {
            // add the hypervisor to the response payload
            jsonArray.add(service_level.toJsonObject());
        }

        response.setStatus(SUCCEED);
        response.setPayload_array(jsonArray);

        return response;
    }

    // Method creates new Virtual machine record in the db
    public Response CreateNewVM(Request request){
        // Get customer data
        Employee employee = dbReadEmployee(request.getUsername());
        System.out.println("Dashboard controller: get employee data from db");

        response.setStatus(dbCreateVirtualMachine(request.getVm_id(), request.getHypervisor(), request.getParams(), employee.getCompany_id(), request.getVm(), request.getService_level()));
        return response;
    }

    // Method updates delete_vm column virtualmachine data in db
    public Response updateDeleteVM(Request request){

        // set delete date for vm in db en return response status
        response.setStatus(dbUpdateDeleteVM(request.getVm()));

        return response;
    }


    /////////////////////////////////**********  Data Request objects   **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Methods returns request object with required data for a ListVMs command to the provisioning API
    public Request listVMs(Request request){
        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());
        request.setCompany_id(employee.getCompany_id());

        // get the hypervisor data from db, depending of the request params
        List<Hypervisor> hypervisors = dbReadHypervisor(CUSTOMER, employee.getCompany_id());

        // loop through the hypervisors
        for (Hypervisor hypervisor : hypervisors) {
            // add the hypervisor ip_address to the request object
            request.setHypervisor(hypervisor.getIp_address());
        }
        return request;
    }


    // Methods returns request object with required data for a getVM command to the provisioning API
    public Request getVM(Request request){
        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        Virtual_Machine virtual_machine = dbReadVirtualMachine(request.getVm());
        // check if requested vm is found in db. otherwise return failed
        if(virtual_machine.getVm_name() != null){
            request.setStatus_request(SUCCEED);
            request.setHypervisor(virtual_machine.getHypervisor_ip());
            request.setVm(virtual_machine.getVm_id());
        }else{
            request.setStatus_request(FAILED);
        }

        return request;
    }

    // Methods returns request object with required data for a newVM command to the provisioning API
    public Request newVM(Request request){
        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        // Check if the if the customer already has a vm with the same name
        List<Virtual_Machine> virtual_machines = dbReadVirtualMachines(employee.getCompany_id());

        boolean duplicate = false;
        for(Virtual_Machine virtual_machine: virtual_machines){
            if (virtual_machine.getVm_name().equals(request.getVm())){
                duplicate = true;
            }
        }

        // Create a UUID for the virtual machine
        request.setVm_id(createUUID("virtual_machine"));

        // continue if no vm is found with the same name
        if(duplicate == false) {

            // get the hypervisor data from db, depending of the request params
            List<Hypervisor> hypervisors = dbReadHypervisor(CUSTOMER, employee.getCompany_id());
            // loop through the hypervisors
            for (Hypervisor hypervisor : hypervisors) {
                // add the hypervisor ip_address to the request object
                request.setHypervisor(hypervisor.getIp_address());
            }
        }else{
            request.setStatus_request(DUPLICATE);
        }
        return request;
    }

    // Methods returns request object with required data for a start VM command to the provisioning API
    public Request startVM(Request request){
        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        // get the hypervisor virtual machine data from db
        Virtual_Machine virtual_machine = dbReadVirtualMachine(request.getVm());
        // check if requested vm is found in db. otherwise return failed
        if(virtual_machine.getVm_name() != null){
            request.setStatus_request(SUCCEED);
            request.setHypervisor(virtual_machine.getHypervisor_ip());
            request.setVm(virtual_machine.getVm_id());
        }else{
            request.setStatus_request(FAILED);
        }

        return request;
    }


    // Methods returns request object with required data for a stop VM command to the provisioning API
    public Request stopVM(Request request){
        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        // get the hypervisor virtual machine data from db
        Virtual_Machine virtual_machine = dbReadVirtualMachine(request.getVm());
        // check if requested vm is found in db. otherwise return failed
        if(virtual_machine.getVm_name() != null){
            request.setStatus_request(SUCCEED);
            request.setHypervisor(virtual_machine.getHypervisor_ip());
            request.setVm(virtual_machine.getVm_id());
        }else{
            request.setStatus_request(FAILED);
        }

        return request;
    }

    public Request deleteVM(Request request){
        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        // get the hypervisor virtual machine data from db
        Virtual_Machine virtual_machine = dbReadVirtualMachine(request.getVm());

        // check if requested vm is found in db. otherwise return failed
        if(virtual_machine.getVm_name() != null){
            request.setStatus_request(SUCCEED);
            request.setHypervisor(virtual_machine.getHypervisor_ip());
            request.setVm(virtual_machine.getVm_id());
        }else{
            request.setStatus_request(FAILED);
        }

        return request;
    }

    // Methods returns request object with required data for a backup VM command to the provisioning API
    public Request backupVM(Request request){
        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        // get the hypervisor virtual machine data from db
        Virtual_Machine virtual_machine = dbReadVirtualMachine(request.getVm());

        // check if requested vm is found in db. otherwise return failed
        if(virtual_machine.getVm_name() != null){
            request.setStatus_request(SUCCEED);
            request.setHypervisor(virtual_machine.getHypervisor_ip());
            request.setVm(virtual_machine.getVm_id());
        }else{
            request.setStatus_request(FAILED);
        }

        return request;
    }


    // Methods returns request object with required data for a modify VM command to the provisioning API
    public Request modifyVM(Request request){

        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        // get the hypervisor virtual machine data from db
        Virtual_Machine virtual_machine = dbReadVirtualMachine(request.getVm());
        // check if requested vm is found in db. otherwise return failed
        if(virtual_machine.getVm_name() != null){
            request.setStatus_request(SUCCEED);
            request.setHypervisor(virtual_machine.getHypervisor_ip());
            request.setVm(virtual_machine.getVm_id());
        }else{
            request.setStatus_request(FAILED);
        }

        return request;
    }

    // Methods returns request object with required data for a modify VM command to the provisioning API
    public Request restoreVM(Request request){

        // get employee data from db
        Employee employee = dbReadEmployee(request.getUsername());

        // get the hypervisor virtual machine data from db
        Virtual_Machine virtual_machine = dbReadVirtualMachine(request.getVm());
        // check if requested vm is found in db. otherwise return failed
        if(virtual_machine.getVm_name() != null){
            request.setStatus_request(SUCCEED);
            request.setHypervisor(virtual_machine.getHypervisor_ip());
            request.setVm(virtual_machine.getVm_id());
        }else{
            request.setStatus_request(FAILED);
        }

        return request;
    }


    /////////////////////////////////**********  Database communication   **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    /////////////////////////////////************************************************\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Method returns data of employee from db
    public Employee dbReadEmployee(String username) {
        Employee employee = new Employee();

        try {
            // prepare db statement to retrieve specific employee
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM employee WHERE username =? ");

            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            // check if db returned data
            if (rs.next()) {
                System.out.println("Dashboard_Dao: Employee records found in db");    // debug comment

                employee.setUsername(rs.getString("username"));
                employee.setRole(rs.getString("role"));
                employee.setFirstname(rs.getString("firstname"));
                employee.setLastname(rs.getString("lastname"));
                employee.setCompany_id(rs.getString("company_id"));
            }else{
                System.out.println("Dashboard_Dao: No Employee records found");    // debug comment
            }
        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: Employee query failed");    // debug comment
            e.printStackTrace();
        }
        return employee;
    }

    // Method returns data of company from db
    public Company dbReadCompany(String company_id) {
        Company company = new Company();

        try {
            // prepare db statement to retrieve specific company
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM company WHERE company_id =? ");

            preparedStatement.setString(1, company_id);
            ResultSet rs = preparedStatement.executeQuery();

            // check if db returned data
            if (rs.next()) {
                System.out.println("Dashboard_Dao: Company records found in db");    // debug comment

                company.setCompany_id(rs.getString("company_id"));
                company.setCompany_name(rs.getString("company_name"));
                company.setZip_code(rs.getString("zip_code"));
                company.setCity(rs.getString("city"));
                company.setAddress(rs.getString("address"));
                company.setHousenumber(rs.getInt("housenumber"));
                company.setWebsite(rs.getString("website"));
                company.setCreate_date(rs.getDate("create_date"));
            }
            else{
                System.out.println("Dashboard_Dao: No Company records found");    // debug comment
            }
        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: Company query failed");    // debug comment
            e.printStackTrace();
        }
        return company;
    }


    // Method returns all templates from db
    public List<Template> dbReadTemplates() {

        // define a new Template list
        List<Template> templates = new ArrayList<Template>();

        // If option equals CUSTOMER, return the hypervisor(s) related to the customer. Otherwise return unused hypervisors
        PreparedStatement preparedStatement;
        try {
            // prepare db statement
            preparedStatement = connection
                    .prepareStatement("SELECT * FROM template");

            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("Dashboard controller: get Template data from db");

            // check if db returned data
            while (rs.next()) {
                Template template = new Template();
                template.setPrice(rs.getFloat("price"));
                template.setTemplate_id(rs.getString("template_id"));
                template.setOperating_system(rs.getString("operating_system"));
                template.setMemory(rs.getFloat("memory"));
                template.setDiskspace(rs.getFloat("diskspace"));
                template.setSpecification(rs.getString("specification"));

                // print the retrieved hypervisors
                System.out.println("Dashboard_Dao: following template found in db: " + template.toJsonString());    // debug comment

                // add the hypervisor to the list
                templates.add(template);
            }

        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: Hypervisor query failed");    // debug comment
            e.printStackTrace();
        }
        return templates;
    }



    /////////////////////////////////**********  CRUD Hypervisor   **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    // Method returns all hypervisor that are in use by a company
    public List<Hypervisor> dbReadHypervisor(String option, String company_id) {

        // define a new hypervisor list
        List<Hypervisor> hypervisors = new ArrayList<Hypervisor>();

        // If option equals CUSTOMER, return the hypervisor(s) related to the customer. Otherwise return unused hypervisors
        PreparedStatement preparedStatement;
        try {
            if(option.equals(CUSTOMER)){
                // prepare db statement to all unused hypervisors
                preparedStatement = connection
                    .prepareStatement("SELECT * FROM hypervisor WHERE company_id = ?");
                preparedStatement.setString(1, company_id);

            }else{
                // prepare db statement to all unused hypervisors
                preparedStatement = connection
                        .prepareStatement("SELECT * FROM hypervisor WHERE company_id IS NULL");
            }

            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("Dashboard controller: get Hypervisor data from db");

            // check if db returned data
            while (rs.next()) {
                Hypervisor hypervisor = new Hypervisor();
                hypervisor.setIp_address(rs.getString("ip_address"));
                hypervisor.setDiskspace(rs.getFloat("diskspace"));
                hypervisor.setMemory(rs.getFloat("memory"));

                // print the retrieved hypervisors
                System.out.println("Dashboard_Dao: following hypervisor found in db: " + hypervisor.toJsonString());    // debug comment

                // add the hypervisor to the list
                hypervisors.add(hypervisor);
            }

        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: Hypervisor query failed");    // debug comment
            e.printStackTrace();
        }
        return hypervisors;
    }

    // Method updates the hypervisor table with the company_id
    public String dbUpdateCompanyHypervisor(String company_id, String hypervisor_ip){
        try {
            // prepare db statement to retrieve specific company
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE hypervisor SET company_id = ? WHERE ip_address = ? ");

            preparedStatement.setString(1, company_id);
            preparedStatement.setString(2, hypervisor_ip);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: Insert company_id in hypervisor query failed");    // debug comment
            e.printStackTrace();
            return FAILED;
        }
        return SUCCEED;
    }


    /////////////////////////////////**********  CRUD Virtual machine   **********\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public Virtual_Machine dbReadVirtualMachine(String vm_id){

        Virtual_Machine virtual_machine = new Virtual_Machine();
        try {
            // prepare db statement to retrieve specific virtual machine
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM virtual_machine WHERE vm_id =?");

            preparedStatement.setString(1, vm_id);
            ResultSet rs = preparedStatement.executeQuery();

            // If db returned data, store data in virtual machine object
            if (rs.next()) {
                System.out.println("Dashboard_Dao: Virtual machine records found in db");    // debug comment
                virtual_machine.setVm_id(rs.getString("vm_id"));
                virtual_machine.setVm_name(rs.getString("vm_name"));
                virtual_machine.setHypervisor_ip(rs.getString("hypervisor_ip"));
                virtual_machine.setTemplate_id(rs.getString("template_id"));
                virtual_machine.setCreate_date(rs.getDate("create_date"));
                virtual_machine.setDelete_date(rs.getDate("delete_date"));
                virtual_machine.setService_level(rs.getString("service_level"));
            }else{
                System.out.println("Dashboard_Dao: No Virtual machine records found");    // debug comment
            }
        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: virtual machine read query failed");    // debug comment
            e.printStackTrace();
        }
        return virtual_machine;
    }

    public List<Virtual_Machine> dbReadVirtualMachines(String company_id){

        List<Virtual_Machine> virtual_machines = new ArrayList<Virtual_Machine>();
        int counter = 0;
        try {
            // prepare db statement to retrieve specific virtual machine
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM virtual_machine WHERE company_id =?");

            preparedStatement.setString(1, company_id);
            ResultSet rs = preparedStatement.executeQuery();

            // If db returned data, store data in virtual machine object
            while (rs.next()) {
                Virtual_Machine virtual_machine = new Virtual_Machine();
                virtual_machine.setVm_id(rs.getString("vm_id"));
                virtual_machine.setVm_name(rs.getString("vm_name"));
                virtual_machine.setHypervisor_ip(rs.getString("hypervisor_ip"));
                virtual_machine.setTemplate_id(rs.getString("template_id"));
                virtual_machine.setCreate_date(rs.getDate("create_date"));
                virtual_machine.setDelete_date(rs.getDate("delete_date"));
                virtual_machine.setService_level(rs.getString("service_level"));

                // Add virtual machine to Array list
                virtual_machines.add(virtual_machine);

                counter = counter + 1;
            }
        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: virtual machine read query failed");    // debug comment
            e.printStackTrace();
        }
        System.out.println("Dashboard_Dao: " + counter + " Virtual machines records found in db");    // debug comment

        return virtual_machines;
    }

    private String dbCreateVirtualMachine(String vm_name, String hypervisor_ip, String template_id, String company_id, String vm_id, String service_level){
        try {
            // prepare db statement to retrieve specific company
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO virtual_machine (vm_name, hypervisor_ip, template_id, company_id, vm_id, service_level)VALUES (?,?,?,?,?,?)");

            preparedStatement.setString(1, vm_name);
            preparedStatement.setString(2, hypervisor_ip);
            preparedStatement.setString(3, template_id);
            preparedStatement.setString(4, company_id);
            preparedStatement.setString(5, vm_id);
            preparedStatement.setString(6, service_level);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: Insert virtual machine query failed");    // debug comment
            e.printStackTrace();
            return FAILED;
        }

        return SUCCEED;
    }


    // Method updates the virtual machine with delete date
    public String dbUpdateDeleteVM(String vm_id){
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        try {
            // prepare db statement to retrieve specific company
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE virtual_machine SET delete_date = ? WHERE vm_id = ? ");

            preparedStatement.setTimestamp(1, time);
            preparedStatement.setString(2, vm_id);
            preparedStatement.toString();
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: set delete date in virtual machine query failed");    // debug comment
            e.printStackTrace();
            return FAILED;
        }
        return SUCCEED;
    }


    // INSERT - Methods creates a UUID for a specific db table
    private String createUUID(String db_table){

        UUID uuid = UUID.randomUUID();
        String UUID = uuid.toString();

        System.out.println("Customer_Dao: Insert data new generated UUID: " + UUID);    // debug comment

        return UUID;
    }




    // Method returns all service levels from db
    public List<Service_Level> dbReadServiceLevels() {

        // define a new service level list
        List<Service_Level> service_levels = new ArrayList<Service_Level>();

        // If option equals CUSTOMER, return the hypervisor(s) related to the customer. Otherwise return unused hypervisors
        PreparedStatement preparedStatement;
        try {
            // prepare db statement
            preparedStatement = connection
                    .prepareStatement("SELECT * FROM service_level");

            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("Dashboard dao: get service levels data from db");

            // check if db returned data
            while (rs.next()) {
                Service_Level service_level = new Service_Level();
                service_level.setName(rs.getString("name"));
                service_level.setCommision(rs.getInt("commision"));


                // print the retrieved hypervisors
                System.out.println("Dashboard_Dao: following template found in db: " + service_level.toJsonString());    // debug comment

                // add the hypervisor to the list
                service_levels.add(service_level);
            }

        } catch (SQLException e) {
            System.out.println("Dashboard_Dao: Service_level query failed");    // debug comment
            e.printStackTrace();
        }
        return service_levels;
    }

}










