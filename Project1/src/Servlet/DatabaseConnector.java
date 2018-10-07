package Servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
	public static final String url = "jdbc:mysql://127.0.0.1:3306/moviedb";
    public static final String userName = "moviedb";
    public static final String password = "moviedb123!";
    
    public static Connection getConnection() {

        Connection returnConn = null;
        try {
            //load the driver
            Class.forName("com.mysql.jdbc.Driver");
            returnConn = DriverManager.getConnection(url, userName, password);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        return returnConn;
    }
}
