package Servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Servlet implementation class MovieDetail
 */
@WebServlet("/MovieDetail")
public class MovieDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    //initailization
    public MovieDetail() {
        super(); 
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String movieid = request.getParameter("movieid");
		
		try{
			Connection databaseConnection = DatabaseConnector.getConnection();
			Statement st = databaseConnection.createStatement();
			ResultSet movie = st.executeQuery("select r.movieid,r.rating, m.title,m.year,m.director"
					+ " from  ratings r, movies m"
					+ " where m.id = '"+movieid+"'"
					+ " and r.movieid = m.id");
			
			/*
			 *  |title|string|
				|year|string|
				|director|string|
				|rating|float|
				|genres|list|
				|stars|list|
			 */
			JSONArray movieList = new JSONArray();
			while(movie.next()){
			
				String mid  = movie.getString("movieid");
				String title = movie.getString("title");
				String year = movie.getString("year");
				String director = movie.getString("director");
				float rating = movie.getFloat("rating");
				
				JSONObject m = new JSONObject();
				m.put("movieid", mid);
				m.put("title", title);
				m.put("year", year);
				m.put("director", director);
				m.put("rating", rating);
				
				/*
				 * 	|genres|list|
				 */
				Statement stmGnr = databaseConnection.createStatement();
				ResultSet genres  = stmGnr.executeQuery("SELECT g.id,g.name "
						+ "FROM genres_in_movies gm, genres g "
						+ "where gm.movieId = '" +mid+ "'"
						+ "and g.id = gm.genreId;");
				JSONArray genre = new JSONArray();  //list of genre
				int gIdx = 0;
				while(genres.next()){
					String gid = genres.getString("id");
					String gname = genres.getString("name");
					
					JSONObject g = new JSONObject();
					g.put("genreid", gid);
					g.put("name", gname);
					
					genre.put(gIdx, g);
					gIdx ++;
				}
				m.put("genres", genre);
				stmGnr.close();
				
			
				/*
				 * |stars|list|
				 */
				Statement stmStars = databaseConnection.createStatement();
				ResultSet resStars = stmStars.executeQuery("SELECT sm.starId, s.name "
						+ "FROM stars s, stars_in_movies sm "
						+ "where sm.movieId = '" +mid+ "'"
						+ "	and s.id = sm.starId;");
				int sindex = 0;
                JSONArray starList = new JSONArray();
                while(resStars.next()){
                    String starid = resStars.getString("starId");
                    String sname = resStars.getString("name");

                    JSONObject star = new JSONObject();
                    star.put("starId", starid);
                    star.put("name", sname);

                    starList.put(sindex, star);
                    sindex++;
                }
                m.put("starlist",starList);
                stmStars.close();
                
                movieList.put(m);
				
			}
			
		st.close();	
		response.getWriter().append(movieList.toString());
		databaseConnection.close();
			
		}catch(SQLException sql){
			sql.printStackTrace();
		}
				
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
