package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
@WebServlet("/JointSearch")
public class JointSearch extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public JointSearch() {
        super();

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*--Read params*/
       
        //http://localhost:8080/Project2/Search?searchType=7&searchData=nm5923559&start=0&end=20&sortType=0
        //int searchType = Integer.parseInt(request.getParameter("searchType"));
        String searchTitle = request.getParameter("searchTitle");
        String searchYear = request.getParameter("searchYear");
        String searchDirector = request.getParameter("searchDirector");
        String searchStar = request.getParameter("searchStar");
        int start = Integer.parseInt(request.getParameter("start")); 			//int?
        int end = Integer.parseInt(request.getParameter("end"));				//String to be parse cannot be null
        int sortType = Integer.parseInt(request.getParameter("sortType"));      //this param cnnot be null

        String select = "select DISTINCT m.id , m.title,m.year,m.director,r.rating " + 
			        		" from  movies m " + 
			        		" LEFT JOIN ratings r ON r.movieid = m.id "
			        		+" LEFT JOIN stars_in_movies sm ON sm.movieId = m.id "+
            		        " LEFT JOIN stars s ON sm.starId = s.id ";
        
        

        //-----------SEARCH BY-----------//
        ArrayList<String> searchParams = new ArrayList<>();  //store the string params
        String where="";
        int searchyear=0;   //0  check this first when setting prepared statement param if not 0 we can add year in
      
        //*******serchYear != null
        if(searchYear!=null&&(!searchYear.equals(""))) {
        	where = select + " where m.year=? ";   //+ where
        	searchyear = Integer.parseInt(searchYear);
        	//searchParams.add(searchStar);
        
	        if(searchStar!=null) {
	        	where = where + " and s.name LIKE ? ";   //+ where
	        	searchStar = "%"+ searchStar +"%";
	        	searchParams.add(searchStar);
	        }
	        if(searchTitle!=null) {
	        	where = where + " and m.title LIKE ? ";
	        	searchTitle = "%"+ searchTitle +"%";
	        	searchParams.add(searchTitle);
	        }
	        if(searchDirector!=null) {
	        	where = where + " and m.director LIKE ? ";
	        	searchDirector = "%"+ searchDirector +"%";
	        	searchParams.add(searchDirector);
	        }
        }
        else if(searchStar!=null) {
	        where = select + " where s.name LIKE ? ";   //+ where
	        searchStar = "%"+ searchStar +"%";
	        searchParams.add(searchStar);
	        	
	        if(searchTitle!=null) {
	        	where = where + " and m.title LIKE ? ";
	        	searchTitle = "%"+ searchTitle +"%";
	        	searchParams.add(searchTitle);
	        }
	        if(searchDirector!=null) {
	        	where = where + " and m.director LIKE ? ";
	        	searchDirector = "%"+ searchDirector +"%";
	        	searchParams.add(searchDirector);
	        } 	
	    }
        else if(searchTitle!=null) {
        	where = select + " where m.title LIKE ? ";
        	searchTitle = "%"+ searchTitle +"%";
        	searchParams.add(searchTitle);
        	
        	if(searchDirector!=null) {
	        	where = where + " and m.director LIKE ? ";
	        	searchDirector = "%"+ searchDirector +"%";
	        	searchParams.add(searchDirector);
	        } 
        }
        else if(searchDirector!=null) {
        	where = select + " and m.director LIKE ? ";
        	searchDirector = "%"+ searchDirector +"%";
        	searchParams.add(searchDirector);
        } 
        else where = select;
        
        
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
            System.out.println(sqlformovie);
            
            if(searchyear!=0) {
            	prestm.setInt(1, searchyear);
            	int index = 0;    //+2
            	
            	
            	
            	
            	for (index=0;index<searchParams.size();index++) {
            		
            		String temp = searchParams.get(index);
            		System.out.println(temp);
            		prestm.setString(index+2, temp);      //2-3-4 ?
            	}
            	
            	prestm.setInt(index+2, limit); 
            	index++;
            	prestm.setInt(index+2, start);
            }
            else {		//searchyear = null;
            	int index = 0;    //+2
            	for (index=0;index<searchParams.size();index++) {
            		String temp = searchParams.get(index);
            		prestm.setString(index+1, temp);      //2-3-4 ?
            	}
            	
            	prestm.setInt(index+1, limit); 
            	index++;
            	prestm.setInt(index+1, start);
            }
            
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