package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        //[s:tt0094859]     |     test:'tt499517'-----[success!]
        String movieid = request.getParameter("movieid");

        try{
            Connection databaseConnection = DatabaseConnector.getConnection();
            //Statement st = databaseConnection.createStatement();
//            ResultSet movie = st.executeQuery("select m.id movieid, m.title,m.year,m.director,r.rating" +
//            		" from  movies m " +
//            		" LEFT JOIN ratings r " +
//            		" ON r.movieid = m.id " +
//            		" where m.id = '"+movieid+"'");

            String sql1 = "select DISTINCT m.id movieid, m.title,m.year,m.director,r.rating " +
                    " from  movies m " +
                    " LEFT JOIN ratings r " +
                    " ON r.movieid = m.id " +
                    " where m.id = ? ";

            PreparedStatement prest1 = databaseConnection.prepareStatement(sql1);
            prest1.setString(1, movieid);
            ResultSet movie = prest1.executeQuery();

			/*
			 *  |title|string|
				|year|string|
				|director|string|
				|rating|float|
				|genres|list|
				|stars|list|
			 */
            JSONArray movieList = new JSONArray();
            int index = 0;
            while(movie.next()){

                String mid  = movie.getString("movieid");
                String title = movie.getString("title");
                String year = movie.getString("year");
                String director = movie.getString("director");
                String rating = movie.getString("rating");

                JSONObject m = new JSONObject();
                m.put("movieid", mid);
                m.put("title", title);
                m.put("year", year);
                m.put("director", director);
                m.put("rating", rating);

				/*
				 * 	|genres|list|
				 */
//                Statement stmGnr = databaseConnection.createStatement();
//                ResultSet genres  = stmGnr.executeQuery("SELECT g.id,g.name "
//                        + "FROM genres_in_movies gm, genres g "
//                        + "where gm.movieId = '" +mid+ "'"
//                        + "and g.id = gm.genreId;");

                String sql2 = "SELECT  g.id,g.name " +
                        "from genres_in_movies gm " +
                        "LEFT JOIN genres g  ON  g.id = gm.genreId " +
                        "where  gm.movieId = ? ";

                PreparedStatement prest2 = databaseConnection.prepareStatement(sql2);
                prest2.setString(1, mid);
                ResultSet genres = prest2.executeQuery();


                JSONArray genre = new JSONArray();  //list of genre
                int gIdx = 0;
                //might return null--------------------[checked]----[scu??]
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
                prest2.close();


				/*
				 * |stars|list|
				 */
//                Statement stmStars = databaseConnection.createStatement();
//                ResultSet resStars = stmStars.executeQuery("SELECT sm.starId, s.name "
//                        + "FROM stars s, stars_in_movies sm "
//                        + "where sm.movieId = '" +mid+ "'"
//                        + "	and s.id = sm.starId;");

                String sql3 = "SELECT sm.starId, s.name from stars s " +
                        " LEFT JOIN stars_in_movies sm ON s.id = sm.starId " +
                        " where sm.movieId = ? ";

                PreparedStatement prest3 = databaseConnection.prepareStatement(sql3);
                prest3.setString(1, mid);
                ResultSet resStars = prest3.executeQuery();


                int sindex = 0;
                JSONArray starList = new JSONArray();
                //might return null---------------[checked]-----[suc?]
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
                prest3.close();

                movieList.put(index, m);
                index++;

            }

            prest1.close();
            response.getWriter().append(movieList.toString());
            databaseConnection.close();

        }catch(SQLException sql){
            sql.printStackTrace();
        }

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

}