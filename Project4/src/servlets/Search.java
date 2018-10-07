package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public Search() {
        super();

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*--Read params*/
        //System.out.println(request.getParameter("searchType"));
        //http://localhost:8080/Project2/Search?searchType=7&searchData=nm5923559&start=0&end=20&sortType=0
        //not fully tested
        int searchType = Integer.parseInt(request.getParameter("searchType"));
        String searchData = request.getParameter("searchData");
        int start = Integer.parseInt(request.getParameter("start")); 			//int?
        int end = Integer.parseInt(request.getParameter("end"));				//String to be parse cannot be null
        int sortType = Integer.parseInt(request.getParameter("sortType"));      //this param cnnot be null

        //New Code for FULL TEXT SEARCH
        String[] words_in_title = null;
        String select = "select DISTINCT m.id , m.title,m.year,m.director,r.rating " +
                " from  movies m " +
                " LEFT JOIN ratings r" +
                " ON r.movieid = m.id ";

        //-----------SEARCH BY-----------//
        String where = "";
        switch(searchType){
            case 0: //==="";[s]
                where = select; //+" where m.id = r.movieId";
                break;
            case 1: //==="title";[s:Bigfoot]
//                where = select +" where m.title LIKE  ? ";  //?cannot find the data ---the start should be 0 as there is only 1 row
                words_in_title = searchData.split(" ");
                //create ?
                where = select +" where MATCH (m.title) AGAINST ( ? IN BOOLEAN MODE) OR edth(?,lower(m.title),CHAR_LENGTH(m.title) div 5 + 1) ";  //?cannot find the data ---the start should be 0 as there is only 1 row
                break;
            case 2: //==="year";[s:2002]
                where = select +" where m.year = ? ";
                break;
            case 3: //==="director";[s:Neil Cohen]
                where = select +" where m.director LIKE ? ";
                searchData = "%"+ searchData +"%";
                break;
            case 4: //==="s.name";	//star's name[s:Marlon Brando=>2results]
                where = select + "LEFT JOIN stars_in_movies sm ON sm.movieId = m.id "+
                        " LEFT JOIN stars s ON sm.starId = s.id "+
                        " where  s.name LIKE ? ";
                searchData = "%"+ searchData +"%";
                break;
            case 5: //==="letter";	//first letter of title[s:B]  //case insensitive
                where = select +" where m.title LIKE ? ";
                searchData = searchData +"%";
                break;
            case 6: //==="genreid";[s:20]
                where = select +"LEFT JOIN genres_in_movies gm ON  m.id = gm.movieId " +
                        "LEFT JOIN genres g  ON  g.id = gm.genreId " +
                        " where  g.id = ? ";
                //searchData = searchData;
                break;
            case 7://==="starid";[s:nm5923559]
                where = select +"LEFT JOIN stars_in_movies sm "
                        + " ON sm.movieId = m.id "
                        + " where sm.starId = ? ";		//starname=? && leftjoin stars on stars.name = sm.starname in new XML
                // searchData = searchData;
                break;
        }

        //--------decide ORDER--------//
        String order="";
        switch(sortType){
            case 0: //==="";
                order = where;
                break;
            case 1: //==="title";
                order = where + " order by m.title ";
                break;
            case 2: //==="rating";
                order = where + " order by r.rating desc ";
                break;
        }

        //-------RANGE---------//
        int limit = end - start;   //start: 0   //might need to check
        if(limit < 0)
            limit = 0;							//check and fix the error
        String range = " limit ? offset ? ;";   //or start-1  "+limit+"  "+start+"

        String sqlformovie = order + range;


        //----------------Connect DATABASE---------------------//
        try{
            Connection databaseConnection = DatabaseConnector.getConnection();
//            Statement st = databaseConnection.createStatement();
//            ResultSet movieRes = st.executeQuery(sqlformovie);

            PreparedStatement prestm = databaseConnection.prepareStatement(sqlformovie);

            //search title
            if(searchType == 1){
                String search = "";
                for(int i=0;i<words_in_title.length;i++){
                    search += "+" + words_in_title[i] + "* ";
                }
                System.out.println("search Data:" + search);
                prestm.setString(1, search);
                prestm.setString(2,searchData.toLowerCase());
                prestm.setInt(3,limit);
                prestm.setInt(4, start);
            }else if(searchType>1 && searchType<8) {
                prestm.setString(1, searchData);
                prestm.setInt(2,limit);
                prestm.setInt(3, start);

            } else {
                prestm.setInt(1, limit);
                prestm.setInt(2, start);
            }

            System.out.println(sqlformovie);
            ResultSet movieRes = prestm.executeQuery();


            JSONArray movieList = new JSONArray();
            int mIdx = 0;
            while(movieRes.next()){
                JSONObject movie = new JSONObject();

                //-----basic info-------//
                String movieid = movieRes.getString("id");
                String title = movieRes.getString("title");
                String year = movieRes.getString("year");
                String director = movieRes.getString("director");
                String rating = movieRes.getString("rating");


                movie.put("id",movieid);
                movie.put("title", title);
                movie.put("year", year);
                movie.put("director", director);
                movie.put("rating", rating);



                //-----|genres|list|-------//
               /* Statement stmGnr = databaseConnection.createStatement();
                ResultSet genres  = stmGnr.executeQuery("SELECT g.id,g.name "
                        + "FROM genres_in_movies gm, genres g "
                        + "where gm.movieId = '" +movieid+ "'"
                        + "and g.id = gm.genreId;");*/

                String sql2 = "SELECT g.id,g.name "
                        + " FROM genres_in_movies gm, genres g "
                        + " where gm.movieId = ? "
                        + " and g.id = gm.genreId;";

                PreparedStatement prest2 = databaseConnection.prepareStatement(sql2);
                prest2.setString(1, movieid);
                ResultSet genres = prest2.executeQuery();


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
                movie.put("genres", genre);
                prest2.close();


                //------|stars|list|-------//
//                Statement stmStars = databaseConnection.createStatement();
//                ResultSet resStars = stmStars.executeQuery("SELECT sm.starId, s.name "
//                        + "FROM stars s, stars_in_movies sm "
//                        + "where sm.movieId = '" +movieid+ "'"
//                        + "	and s.id = sm.starId;");

                String sql3 = "SELECT sm.starId, s.name "
                        + "FROM stars s, stars_in_movies sm "
                        + "where sm.movieId = ? "
                        + "	and s.id = sm.starId;";

                PreparedStatement prest3 = databaseConnection.prepareStatement(sql3);
                prest3.setString(1, movieid);
                ResultSet resStars = prest3.executeQuery();

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
                prest3.close();

                movieList.put(mIdx,movie);
                mIdx++;
            }

            prestm.close();


            databaseConnection.close();
            response.getWriter().append(movieList.toString());

        }catch(SQLException sql){
            sql.printStackTrace();
        }

        //response.getWriter().append("Served at: ").append(request.getContextPath());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

}