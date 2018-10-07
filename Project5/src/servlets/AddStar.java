package servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

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
		int error = 0;
		try {
			// Connection conn = DatabaseConnector.getConnection();

			// Context initCtx = new InitialContext();
			//
			//
			// Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// if (envCtx == null) {
			// message_result.put("message","envCtx is NULL");
			// error = 1;
			// }
			// //System.out.println("envCtx is NULL");
			//
			// // Look up our data source
			// DataSource ds = (DataSource) envCtx.lookup("jdbc/WriteDB");
			//
			// if (ds == null) {
			// message_result.put("message","ds is NULL");
			// error = 1;
			// }
			// //System.out.println("ds is null.");
			//
			// Connection dbcon = ds.getConnection();

			Connection dbcon = DBWriteConnector.getConnection();

			if (dbcon == null) {
				message_result.put("message", "dbcon is NULL");
				error = 1;
			}

			// System.out.println("dbcon is null.");

			// -------------INSERT The star-------------------[success]----------Jaden Smith
			String procinsert = "{call proc_InsertStar(?,?)}";
			CallableStatement cs = dbcon.prepareCall(procinsert);

			cs.setString(1, starName);
			if (!starYear.equals(""))
				cs.setInt(2, Integer.parseInt(starYear));
			else
				cs.setNull(2, java.sql.Types.VARCHAR);

			ResultSet res = cs.executeQuery();
			if (res == null)
				message_result.put("message", "error occurs: resultset is null!");

			while (res.next()) {
				String te = res.getString(1);
				System.out.println("te:" + te);

				if (te.equals("STAR INSERTED")) {
					message_result.put("result", "success");
					message_result.put("message", "This star is inserted successfully");
					response.getWriter().append(message_result.toString());
					return;

				}
			}

			dbcon.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		message_result.put("result", "fail");
		if (error == 0)
			message_result.put("message", "error occurs: cannot insert the star!");

		response.getWriter().append(message_result.toString());

	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
