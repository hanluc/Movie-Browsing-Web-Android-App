package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet("/Cart")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public ShoppingCartServlet() {
        super();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //http://localhost:8080/Project2/Cart?movieid=tt0094859&operation=0
        HttpSession session=request.getSession();
        int operation = Integer.parseInt(request.getParameter("operation"));    //cannot be null!!
        String movieId = request.getParameter("movieid");						//[s:tt0094859]
        boolean checkout = false;


        //reference to session contents
        HashMap<String,JSONObject> Items = (HashMap<String,JSONObject>) session.getAttribute("ListOfItems");

        if(Items == null)
        {
            Items = new HashMap<String,JSONObject>();
            session.setAttribute("ListOfItems", Items);
        }

        //[1]-----Decide Operation & modify the session Set----//
        switch(operation){
            case 0://--add new item & get new item details
                JSONObject new_movie = new JSONObject();
                new_movie.put("number", "1");
                Items.put(movieId, new_movie);

			/*database operation
			 *
			 */
                try{
                    Connection dbConnection = DatabaseConnector.getConnection();
                    Statement stm = dbConnection.createStatement();
                    ResultSet resMovie = stm.executeQuery("select m.title from movies m where m.id='" +movieId+ "'");
                    if(resMovie.next()){
                        String title = resMovie.getString("title");
                        //******debug**********
                        System.out.println("the title of new movie is :" + title);
                        new_movie.put("title", title);
                    }
                    stm.close();
                    dbConnection.close();

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case 1://--delete item
                Items.remove(movieId);
                break;
            case 2://--add 1 to existing items
                JSONObject movie = Items.get(movieId);
                if(movie == null)					//check if the movie really exist
                    break;
                int num = Integer.parseInt(movie.getString("number"));
                num++;
                movie.put("number", Integer.toString(num));
                Items.replace(movieId, movie);
                break;
            case 3://--minus 1 from existing items--- & ---delete if n goes down to 0--//
                JSONObject movie2 = Items.get(movieId);
                if(movie2 == null)					//check if the movie really exist
                    break;
                int num2 = Integer.parseInt(movie2.getString("number"));
                num2--;
                if(num2 <= 0) //if number <=0, delete this movie from hashmap
                    Items.remove(movieId);
                else{ 		//modify
                    movie2.put("number", Integer.toString(num2));
                    Items.replace(movieId, movie2);
                }
                break;
            case 4://do nothing & let servlet give back previous info
                break;
            case 5://clear cart  used only in checkout.
                session.setAttribute("ListOfItems", null);		//set origin reference in session =null
                //assume the user has logined in
                //[5.1]check if login
                String isLogin = (String) session.getAttribute("isLogin");
                System.out.println(isLogin);

                //set your login state here
	/*test*/ // isLogin="true";

                //[5.2]if login
                if(isLogin.equals("true")){

                    String username = (String) session.getAttribute("username");

                    //set your test username here
	/*test*/ //	username = "a@email.com";

                    System.out.println("login true,start connection with db");

                    try{
                        Connection databaseConnector = DatabaseConnector.getConnection();
                        Statement statement = databaseConnector.createStatement();
                        ResultSet userSet = statement.executeQuery("SELECT id FROM customers " +
                                "WHERE email = '" + username + "'");
                        //set user id
                        //[5.2.1]retrieve session username to get userid from db
                        int userid = -1;
                        if(userSet.next()){
                            userid = userSet.getInt("id");		//?reference?
                        }
                        System.out.println(userid);   //961?

                        //[5.2.2]retrieve the largest number of record from database
                        ResultSet numberSet = statement.executeQuery("SELECT id FROM moviedb.sales"
                                + " order by id desc limit 1");
                        int recordid = 0;
                        if(numberSet.next()){
                            recordid = numberSet.getInt("id");
                        }
                        statement.close();
                        System.out.println(recordid);

                        //[5.2.3]set this record number & get Timestamp
                        int recordnum = recordid+1;
                        Timestamp sqlDate = new Timestamp(new java.util.Date().getTime());

                        //[5.2.4]insert record in loop & add record id (++1 for each loop)
                        JSONArray boughtMovieList = new JSONArray();
                        int bIdx = 0;
                        if(Items !=null){      //when cart is not empty.
                            Iterator iter=Items.entrySet().iterator();

                            String ins ="INSERT INTO sales VALUES(?,?,?,?)";
                            PreparedStatement pstm = databaseConnector.prepareStatement(ins);

                            while(iter.hasNext()){
                                //get key(movieId) and value(number+title)
                                Map.Entry entry0 = (Map.Entry) iter.next();
                                String key0 = (String) entry0.getKey();
                                JSONObject val0 = (JSONObject) entry0.getValue();

                                //--add value(recordid+movieid) &add it to response' list
                                val0.put("recordid", recordnum);
                                val0.put("id", key0);
                                boughtMovieList.put(bIdx,val0);   	//ADD to list

                                //insert record
                                pstm.setInt(1,recordnum);
                                pstm.setInt(2,userid);
                                pstm.setString(3,key0);
                                pstm.setTimestamp(4,sqlDate);
                                pstm.executeUpdate();			//INSERT


                                bIdx++;
                                recordnum++;
                            }
                        }
                        //[5.2.5]----RESPOND the MovieList----//
                        response.getWriter().append(boughtMovieList.toString());
                        checkout = true;  //has checked out
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    //[5.2.6]--set the session's Map to null
                    Items = null;

                }
                else{//do nothing and give back message.
                    response.getWriter().append("Login first my dear:)");
                    //response.sendRedirect("/Login");
                }

                break;									//set reference in this code=null
            case 6://modify number directly
                JSONObject movieToModify = Items.get(movieId);
                String numToModify = request.getParameter("number");

                if(Integer.parseInt(numToModify) <= 0){
                    Items.remove(movieId);
                }
                else{
                    movieToModify.put("number", numToModify);
                }

        }


        //[2]----generate MovieList from Session's Set---id, title, number
        JSONArray MovieList = new JSONArray();

        if(Items !=null){     				 //when cart is not empty==>hasn't checkout
            Iterator iter=Items.entrySet().iterator();
            int Idx = 0;
            while(iter.hasNext()){

                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                JSONObject val = (JSONObject) entry.getValue();

                val.put("id", key);
                MovieList.put(Idx,val);

                //*******deug******
                System.out.println(val.toString());

                Idx++;
            }

        }


        //[3]----RESPOND the MovieList---if hasn't checkout; it means response hasn't been written-//
        if(!checkout)
            response.getWriter().append(MovieList.toString());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }

}