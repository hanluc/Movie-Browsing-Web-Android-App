package servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class AddStar
 */
@WebServlet("/AddStar")
public class AddStar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AddStar() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String starName = request.getParameter("starName"); // attribute:name="starName"
		String starYear = request.getParameter("starYear");

		System.out.println(starName + "  " + starYear);
		
		JSONObject message_result = new JSONObject();
		try {
			Connection conn = DatabaseConnector.getConnection();
					    
			    //-------------INSERT The star-------------------[success]----------Jaden Smith
			    String procinsert = "{call proc_InsertStar(?,?)}";
			    CallableStatement cs = conn.prepareCall(procinsert);
			    
			    cs.setString(1, starName);
			    if(!starYear.equals(""))
			    	cs.setInt(2, Integer.parseInt(starYear));
			    else
			    	cs.setNull(2, java.sql.Types.VARCHAR);
			   
			    ResultSet res = cs.executeQuery();
			    
			    while(res.next()) {
			    	String te = res.getString(1);
			    	System.out.println("te:"+te);  
			        
			        if(te.equals("STAR INSERTED"))
			        {
			        	message_result.put("result", "success");
				    	message_result.put("message","This star is inserted successfully");
				    	response.getWriter().append(message_result.toString());
				    	return;
				    	
			        }
			}
	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		message_result.put("result", "fail");
    	message_result.put("message","error occurs: cannot insert the star!");
		
    	response.getWriter().append(message_result.toString());

		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
