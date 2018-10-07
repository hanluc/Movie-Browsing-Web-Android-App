package servlets;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
 * Servlet implementation class AddMovie
 */
@WebServlet("/AddMovie")
public class AddMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AddMovie() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// need get
		// String mid = request.getParameter("movieid");
		String title = request.getParameter("mtitle");
		String year = request.getParameter("myear");
		String director = request.getParameter("mdirector");
		String gname = request.getParameter("genreName");
		String sname = request.getParameter("starName");

		String mid = "";
		String starid = "";

		JSONObject message_result = new JSONObject();
		try {
			// Connection conn = DatabaseConnector.getConnection();

			// Context initCtx = new InitialContext();
			//
			// Context envCtx = (Context) initCtx.lookup("java:comp/env");
			// if (envCtx == null)
			// System.out.println("envCtx is NULL");
			//
			// // Look up our data source
			// DataSource ds = (DataSource) envCtx.lookup("jdbc/WriteDB");
			//
			// if (ds == null)
			// System.out.println("ds is null.");
			//
			// Connection dbcon = ds.getConnection();
			// if (dbcon == null)
			// System.out.println("dbcon is null.");

			Connection dbcon = DBWriteConnector.getConnection();

			// -----------call the procedure--------
			int myear = 0;
			if (!year.equals(""))
				myear = Integer.parseInt(year);
			try {
				// conn = DbConn.getDbConn();//get pool conn
				String procStr = "{call ADD_MOVIE_IFNE_INCRE(?,?,?,?,?)}";
				CallableStatement cstmt = dbcon.prepareCall(procStr);

				cstmt.setString(1, title);
				cstmt.setString(2, director);
				cstmt.setInt(3, myear);
				cstmt.setString(4, sname);
				cstmt.setString(5, gname);

				ResultSet rs = cstmt.executeQuery();

				while (rs.next()) {
					String te = rs.getString(1);
					System.out.println("te:" + te);

					if (te.equals("MOVIE CREATED")) {
						message_result.put("result", "success");
						message_result.put("message", "This star is inserted successfully");
						response.getWriter().append(message_result.toString());
						return;

					}

				}

			} catch (Exception e) {
				System.out.println("e: " + e);
			}

			dbcon.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		message_result.put("result", "fail");
		message_result.put("message", "error occurs: cannot insert the star!");
		response.getWriter().append(message_result.toString());

		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
