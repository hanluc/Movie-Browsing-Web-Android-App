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
 * Servlet implementation class MovieList
 */
@WebServlet("/MovieList")
public class MovieList extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public MovieList() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //get the top number of movies, this time is 20
        String topNumber = request.getParameter("topNumber");
        //create a movie list to store movies
        //establish the connection to database and do the query operation
        try{
            //get the connection
            Connection databaseConnection = DatabaseConnector.getConnection();
            Statement statement = databaseConnection.createStatement();
            //find basic info of top20 movies
            ResultSet res1 = statement.executeQuery("select r.movieid,r.rating, m.title,m.year,m.director"
                    + " from ratings r, movies m"
                    + " where r.movieid = m.id"
                    + " order by r.rating desc limit 20");

            JSONArray movieList = new JSONArray();
            while (res1.next()){
                String movieid = res1.getString("movieid");
                String rating = res1.getString("rating");
                String title = res1.getString("title");
                int year = res1.getInt("year");
                String director = res1.getString("director");

                JSONObject movie = new JSONObject();
                movie.put("movieid",movieid);
                movie.put("rating", rating);
                movie.put("title", title);
                movie.put("year", year);
                movie.put("director", director);


                //find&add corresponding genres
                Statement stmGenres = databaseConnection.createStatement();
                ResultSet resGenres = stmGenres.executeQuery("select gm.genreId,gm.movieid,g.name"
                        + " from genres_in_movies gm, genres g"
                        + " where gm.genreId = g.id"
                        + " and gm.movieid='"+ movieid + "'");

                //generate genres list
                int gindex = 0;
                JSONArray genreList = new JSONArray();
                while(resGenres.next()){
                    String genreId = resGenres.getString("genreId");
                    String gname = resGenres.getString("name");

                    JSONObject genre = new JSONObject();
                    genre.put("genreId", genreId);
                    genre.put("name", gname);

                    genreList.put(gindex, genre);
                    gindex++;
                }
                movie.put("genrelist",genreList);
                stmGenres.close();

                //find&add stars
                Statement stmStars = databaseConnection.createStatement();
                ResultSet resStars = stmStars.executeQuery("select sm.starId,s.name"
                        + " from stars_in_movies sm, stars s"
                        + " where sm.starId = s.id"
                        + " and sm.movieid='"+movieid+"'");

                //generate list
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
                movie.put("starlist",starList);
                stmStars.close();
                movieList.put(movie);
            }
            response.getWriter().append(movieList.toString());

            statement.close();
            databaseConnection.close();

        }catch (SQLException sql){
            sql.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }


}