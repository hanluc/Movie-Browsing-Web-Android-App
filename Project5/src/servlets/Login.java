package servlets;


import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//add google Recaptcha
		try {
			HttpSession session = request.getSession();
			String isLogin = (String)session.getAttribute("isLogin");
			if(isLogin == null || !isLogin.equals("true"))
			{
				//Recaptcha
//				String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//				System.out.println(gRecaptchaResponse);
//				RecaptchaVerifyUtils.verify(gRecaptchaResponse);
			}
		} catch (Exception e) {
			JSONObject result = new JSONObject();
			result.put("result","fail");
			result.put("errorcode","recaptha");
			result.put("errormessage","recaptcha verification error");
			response.getWriter().append(result.toString());
			return;
		}

		try {
			//
			HttpSession session = request.getSession();
			String isLogin = (String) session.getAttribute("isLogin");
			//
			JSONObject result = new JSONObject();
			if(isLogin == null){
				//this mean this is the first time try to login in
				isLogin = "false";
				session.setAttribute("isLogin",isLogin);
			}

			//test if the user have login in
			if(isLogin.equals("true")){
				result.put("result","success");
				result.put("successcode","1");//this represent one user have successfully login
				result.put("username",session.getAttribute("username"));
			}else {
				//get the username and password
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				if(username == null || password == null){
					//this means this is to test if the user has login in
					result.put("result","fail");
					result.put("errorcode","1");
					result.put("errormessage","user should login in first");
				}else {
					//use want to login in
//					String sql = "SELECT password FROM customers WHERE email = ?";
//					Connection databaseConnector = DatabaseConnector.getConnection();
//					PreparedStatement statement = databaseConnector.prepareStatement(sql);
//					statement.setString(1,username);
//					ResultSet resultSet = statement.executeQuery();
//					String correctPassword = null;
//					while (resultSet.next()){
//						correctPassword = resultSet.getString("password");
//						System.out.println(correctPassword);
//					}
//					statement.close();
//					databaseConnector.close();
					Boolean verifyResult = VerifyPassword.verifyCredentials(username,password);
					System.out.println("verify result" + verifyResult);
					if(!verifyResult){
						//wrong password
						result.put("result","fail");
						result.put("errorcode","3");
						result.put("errormessage","Wrong usernpassword!");
					}else {
						//successfully login
						result.put("result","success");
						result.put("successcode","2");
						//set session
						session.setAttribute("isLogin","true");
						session.setAttribute("username",username);
					}
				}

			}
			response.getWriter().append(result.toString());

		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
