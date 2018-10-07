package Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Servlet implementation class StarDetail
 */
@WebServlet("/StarDetail")
public class StarDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
 
    public StarDetail() {
        super();
    }

	/*
	 * Request parameters
|Name|Type|
|---|---|
|movieid|String|

#### Return parameters
|Name|Type|
|---|---|
|name|string|
|birthyear|string|
|movies|list|

##### each movie in stars
|Name|Type|
|---|---|
|movieid|string|(non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String starid = request.getParameter("starid");
		
		try{
			Connection databaseConnection = DatabaseConnector.getConnection();
			Statement stmStar = databaseConnection.createStatement();
			ResultSet reStar = stmStar.executeQuery("SELECT * FROM stars s where s.id = '"+ starid +"';");
			
			JSONObject starDetail = new JSONObject();
			if(reStar.next()){
				String name = reStar.getString("name");
				String birthyear = reStar.getString("birthyear");
				
				starDetail.put("name", name);
				starDetail.put("birthyear", birthyear);
				
				//------movieList----------//
				Statement stmMovies = databaseConnection.createStatement();
				ResultSet resMovies = stmMovies.executeQuery("SELECT * "
						+ "FROM stars_in_movies sm "
						+ "where sm.starId = '" +starid+ "';");
				JSONArray movieList = new JSONArray();
				int mIdx = 0;
				while(resMovies.next()){
					String movieId = resMovies.getString("movieid");
					JSONObject movie = new JSONObject();
					movie.put("movieid", movieId);
					movieList.put(mIdx,movie);
					mIdx++;
				}
				starDetail.put("movies", movieList);
				stmMovies.close();
				
			}
			
			stmStar.close();
			databaseConnection.close();
			
			response.getWriter().append(starDetail.toString());
			
		}catch (SQLException sql){
			sql.printStackTrace();
		}
		
		
		
		
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
