package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


/**
 * Servlet implementation class VerifyCardServlet
 */
@WebServlet("/VerifyCard")
public class VerifyCardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public VerifyCardServlet() {
        super();

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String first = request.getParameter("first");
        String last = request.getParameter("last");
        String cardNum = request.getParameter("cardNum");
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");

        System.out.println(first+" "+last);       //request header&body?
        System.out.println(cardNum);

        try{
           // Connection databaseConnector = DatabaseConnector.getConnection();
            Connection databaseConnector = DBReadConnector.getConnection();
            
//            Statement statement = databaseConnector.createStatement();
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM creditcards " +
//                    //" WHERE firstName = 'Bill' "
//                    //+ " and lastName = 'Wang'");
//                    " WHERE id = '"+ cardNum +"' ");
//            
            String sql = "SELECT * FROM creditcards  WHERE id = ? ";
            PreparedStatement prest= databaseConnector.prepareStatement(sql);
            prest.setString(1, cardNum);
            ResultSet resultSet  = prest.executeQuery();

            //System.out.println(resultSet.getDate("expiration"));

            String firstName;
            String lastName;
            Date expiration;
            JSONObject result = new JSONObject();

            //----INSPECT result----
            if(resultSet.next()){
                System.out.println(resultSet.getDate("expiration"));
                firstName = resultSet.getString("firstName");
                lastName = resultSet.getString("lastName");
                expiration = resultSet.getDate("expiration");

                //change date format
                Calendar cal = Calendar.getInstance();
                cal.setTime(expiration);
                System.out.println(cal);
                int dbmonth = cal.get(Calendar.MONTH)+1;    //has a offest of -1
                int dbday = cal.get(Calendar.DAY_OF_MONTH);
                int dbyear = cal.get(Calendar.YEAR);

                //System.out.println(month+day+year+" ?= "+dbmonth+" "+dbday+" "+dbyear);
                //System.out.println(Integer.parseInt(month)==dbmonth);
                //System.out.println(Integer.parseInt(day)==dbday);
                //(Integer.parseInt(year)!=dbyear)++(Integer.parseInt(day)!=dbday

                if (firstName == null){
                    //there is no such card
                    result.put("result","fail");
                    result.put("statecode","1");
                    result.put("message","The card number doesn't exist!");
                }
                //---name does not equal
                else if(!(firstName.equals(first)&&lastName.equals(last))){
                    //wrong name
                    result.put("result","fail");
                    result.put("statecode","2");
                    result.put("message","The card holder is wrong!");
                }
                else if(Integer.parseInt(year)!=dbyear||Integer.parseInt(month)!=dbmonth||Integer.parseInt(day)!=dbday){
                    result.put("result","fail");
                    result.put("statecode","3");
                    result.put("message","Expiration date wrong!");
                }
                else {
                    //successfully login
                    result.put("result","success");
                    result.put("statecode","0");
                    result.put("message", "this card is added succeessfully");

                }

            }
            else//---result == null no such card.
            {
                result.put("result","fail");
                result.put("statecode","1");
                result.put("message","The card number doesn't exist!");
            }

            databaseConnector.close();
            response.getWriter().append(result.toString());

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws IOException, ServletException {
        //test post;
        this.doPost(request, response);//wired postman cannot give right back
    }

}