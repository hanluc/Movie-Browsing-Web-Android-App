package servlets;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.json.JSONException;
import org.json.JSONObject;

import org.jasypt.util.password.StrongPasswordEncryptor;   //configue the maven and it will be ok

/**
 * Servlet implementation class Login
 */
@WebServlet("/ManagerLogin")
public class ManagerLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManagerLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String username = request.getParameter("username");
		String passwd = request.getParameter("password");
		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		
		HttpSession ss = request.getSession();
		
		String manlogin = (String) ss.getAttribute("manlogin");
		//String isLogin = (String) ss.getAttribute("isLogin");  //has user priority
		
		if(manlogin==null) {
			manlogin = "false";
			ss.setAttribute("manlogin", manlogin);
		}

		
		PrintWriter out = response.getWriter();
		
		//Verify reCAPTCHA
		try {
			RecaptchaVerifyUtils.verify(gRecaptchaResponse);
		}catch(Exception e) {
			out.println("<html>");
			out.println("<head><title>Error</title><head>");
			out.println("<body>");
			out.println("<p>Verification error</p>");
			out.println("<p>"+e.getMessage()+"</p>");
			out.println("</body>");
			out.println("</html");
			out.close();
			
			return;
		}
		
		//get connection with database
		try {
			Connection conn= DatabaseConnector.getConnection();
			String query = "select * from employees where email = ?";
			PreparedStatement stm = conn.prepareStatement(query);
			stm.setString(1, username);
			ResultSet res = stm.executeQuery();
			
			System.out.print("In login servlet:");
			
			//verify password
			boolean success = false;
			if(res.next()) {
				String encryptedpassword  = res.getString("password");
				
				success = new StrongPasswordEncryptor().checkPassword(passwd, encryptedpassword);
				
			}
	
			//store the result
			JSONObject result = new JSONObject();
			if(success) {      //string need to use .equal to compare contents
				try {
					result.put("Result", "success");
					result.put("Message", "Congratulations!");
					
					manlogin = "true";
					ss.setAttribute("manlogin", manlogin);
					
					System.out.print("Manager successfully logged in!");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else {
				try {
					result.put("Result", "fail");
					result.put("Message", "The manager does not exits or password wrong!");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			stm.close();
			conn.close();
		
		System.out.print("Manager login servlet done!");
			
		response.getWriter().append(result.toString());
		out.close();
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doPost(request, response);
	}

}
