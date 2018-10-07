package servlets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetMoviePoster
 */
@WebServlet("/AutoComplete")
public class AutoComplete extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AutoComplete() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        JSONArray suggestions = new JSONArray();
        // return the empty json array if query is null or empty
        if (query == null || query.trim().isEmpty()) {
            response.getWriter().write(suggestions.toString());
            return;
        }
        String fullTextSearch = "";
        String[] searchData = query.split(" ");
        for(int i=0;i<searchData.length;i++) {
            fullTextSearch += "+" + searchData[i] + "* ";
        }
        System.out.println("autoauto " + fullTextSearch);
        String sql = "select *  from  movies where MATCH (title) AGAINST ( ? IN BOOLEAN MODE)  limit ?";
        try {
            Connection connection = DatabaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,fullTextSearch);
            //limit is 10
            preparedStatement.setInt(2,10);
            System.out.println(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
               addSuggestions(resultSet,suggestions);
            }
            preparedStatement.close();
            //if there is no enough full text search do the fuzzy search
            int suggLen = suggestions.length();
            if(suggLen < 10){
                //calculate the th
                String sqlUDF = "select * from movies where edth(lower(title),?,CHAR_LENGTH(title) div 5 + 1)";
                PreparedStatement preparedStatementUDF = connection.prepareStatement(sqlUDF);
                preparedStatementUDF.setString(1,query.toLowerCase());
                ResultSet udfResult = preparedStatementUDF.executeQuery();
                while (udfResult.next() && suggestions.length() <10)
                {
                   if(!suggestions.toString().contains(udfResult.getString("id"))){
                       addSuggestions(udfResult,suggestions);
                   }
                }
            }


            response.getWriter().write(suggestions.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


    private void addSuggestions(ResultSet resultSet,JSONArray suggestions) throws Exception{
        JSONObject movie = new JSONObject();
        movie.put("value",resultSet.getString("title"));
        JSONObject data = new JSONObject();
        data.put("category","Movie");
        data.put("id",resultSet.getString("id"));
        movie.put("data",data);
        suggestions.put(movie);
    }

}
