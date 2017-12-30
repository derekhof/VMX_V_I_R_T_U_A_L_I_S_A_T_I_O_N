package Data_Access_Objects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Derek on 20-7-2017.
 */
public class DBConnectionManager {

    private Connection connection;

    public DBConnectionManager() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://db.vmx.local:3306/hva_vmx";
        Class.forName("com.mysql.jdbc.Driver");
        this.connection = DriverManager.getConnection(url,"vmx_user","Team_blaaskaak");
    }

    public Connection getConnection(){
        return this.connection;
    }
}
