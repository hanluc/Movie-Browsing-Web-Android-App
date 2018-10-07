package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
	|starid|String|
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
        //[s:nm0000001]
        String starid = request.getParameter("starid");

        try{
            Connection databaseConnection = DatabaseConnector.getConnection();
            //Statement stmStar = databaseConnection.createStatement();
            //----star----//
            //ResultSet reStar = stmStar.executeQuery("SELECT * FROM stars s where s.id = '"+ starid +"';");

            String sql1 = "SELECT * FROM stars s where s.id = ? ;";
            PreparedStatement prest1 = databaseConnection.prepareStatement(sql1);
            prest1.setString(1, starid);
            ResultSet reStar = prest1.executeQuery();


            JSONObject starDetail = new JSONObject();
            if(reStar.next()){

                //----this star's basic info---//
                String name = reStar.getString("name");
                String birthyear = reStar.getString("birthyear");

                starDetail.put("name", name);
                starDetail.put("birthyear", birthyear);



                //------movieList of this star----------//
//                Statement stmMovies = databaseConnection.createStatement();
//                ResultSet resMovies = stmMovies.executeQuery("SELECT DISTINCT m.id, m.title, m.year, m.director,r.rating "
//                        + "FROM stars_in_movies sm, ratings r, movies m"
//                        + " where r.movieId = sm.movieId"
//                        + " and m.id = r.movieId"
//                        + " and sm.starId = '" +starid+ "';");


                String selectmovie = "SELECT DISTINCT m.id, m.title, m.year, m.director,r.rating " +
                        " FROM movies m " +
                        " LEFT JOIN ratings r ON m.id = r.movieId " +
                        " LEFT JOIN stars_in_movies sm ON m.id = sm.movieId " +
                        " where sm.starId = ? ";
                PreparedStatement prestm = databaseConnection.prepareStatement(selectmovie);
                prestm.setString(1, starid);
                ResultSet resMovies = prestm.executeQuery();


                JSONArray movieList = new JSONArray();
                int mIdx = 0;
                while(resMovies.next()){

                    //--basic info of MOVIE--//
                    String movieId = resMovies.getString("id");
                    String title = resMovies.getString("title");
                    String year = resMovies.getString("year");
                    String director = resMovies.getString("director");
                    String rating = resMovies.getString("rating");

                    JSONObject m = new JSONObject();

                    m.put("movieid", movieId);
                    m.put("title", title);
                    m.put("year", year);
                    m.put("director", director);
                    m.put("rating", rating);

					/*
					 * 	|genres|list|
					 */
//                    Statement stmGnr = databaseConnection.createStatement();
//                    ResultSet genres  = stmGnr.executeQuery("SELECT g.id,g.name "
//                            + "FROM genres_in_movies gm, genres g "
//                            + "where gm.movieId = '" +movieId+ "'"
//                            + "and g.id = gm.genreId;");
                    JSONArray genreList = new JSONArray();  //list of genre

                    String sql2 = "SELECT  g.id,g.name " +
                            "from genres_in_movies gm " +
                            "LEFT JOIN genres g  ON  g.id = gm.genreId " +
                            "where  gm.movieId = ? ";

                    PreparedStatement prest2 = databaseConnection.prepareStatement(sql2);
                    prest2.setString(1, movieId);
                    ResultSet genres = prest2.executeQuery();

                    int gIdx = 0;
                    while(genres.next()){
                        String gid = genres.getString("id");
                        String gname = genres.getString("name");

                        JSONObject g = new JSONObject();
                        g.put("genreid", gid);
                        g.put("name", gname);

                        genreList.put(gIdx, g);
                        gIdx ++;
                    }

                    m.put("genres", genreList);
                    prest2.close();


					/*
					 * |stars|list|
					 */
//                    Statement stmStars = databaseConnection.createStatement();
//                    ResultSet resStars = stmStars.executeQuery("SELECT sm.starId, s.name "
//                            + "FROM stars s, stars_in_movies sm "
//                            + "where sm.movieId = '" +movieId+ "'"
//                            + "	and s.id = sm.starId;");

                    String sql3 = "SELECT sm.starId, s.name from stars s " +
                            " LEFT JOIN stars_in_movies sm ON s.id = sm.starId " +
                            " where sm.movieId = ? ";

                    PreparedStatement prest3 = databaseConnection.prepareStatement(sql3);
                    prest3.setString(1, movieId);
                    ResultSet resStars = prest3.executeQuery();

                    int sindex = 0;
                    JSONArray starList = new JSONArray();
                    while(resStars.next()){
                        String starId = resStars.getString("starId");
                        String sname = resStars.getString("name");

                        JSONObject star = new JSONObject();
                        star.put("starId", starId);
                        star.put("name", sname);

                        starList.put(sindex, star);
                        sindex++;
                    }

                    m.put("starlist",starList);
                    prest3.close();


                    movieList.put(mIdx, m);
                    mIdx++;
                }


                starDetail.put("movies", movieList);
                prestm.close();

            }

            prest1.close();
            databaseConnection.close();

            response.getWriter().append(starDetail.toString());

        }catch (SQLException sql){
            sql.printStackTrace();
        }

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

}