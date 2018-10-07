package servlets;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBReadConnector {
	
	 public static Connection getConnection() throws NamingException {

	        Connection returnConn = null;
	        try {
	        	Context initCtx = new InitialContext();

	            Context envCtx = (Context) initCtx.lookup("java:comp/env");
	            if (envCtx == null)
	            	System.out.println("envCtx is NULL");
	                //System.out.println("envCtx is NULL");

	            // Look up our data source
	            DataSource ds = (DataSource) envCtx.lookup("jdbc/TestDB");
	            
	            if (ds == null)
	            	System.out.println("ds is NULL");
	                //System.out.println("ds is null.");

	            returnConn = ds.getConnection();
	            
	        } catch (NamingException e) {
	            e.printStackTrace();
	        } catch (SQLException e1) {
	            e1.printStackTrace();
	        }

	        return returnConn;
	    }
}