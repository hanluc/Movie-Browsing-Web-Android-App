package servlets;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetMoviePoster
 */
@WebServlet("/GetStarImage")
public class GetStarImage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStarImage() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String starid = request.getParameter("starid");
        String posterUrl = getStarImage(starid);
        JSONObject moviePoster = new JSONObject();
        moviePoster.put("starid",starid);
        moviePoster.put("image",posterUrl);
        response.getWriter().append(moviePoster.toString());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    //input movieId and use htmlunit to do the scraper and get the url for image
    private String getStarImage(String starId){
        String posterUrl = "";
        try {
            //first, Get the url by the id
            String htmlUrl = "https://www.imdb.com/name/"+starId+"/";
            URL url = new URL(htmlUrl);
            InputStream in =url.openStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader bufr = new BufferedReader(isr);
            Boolean find = false;//to show if we find the specific label
            //to collect the string which may contain the url
            String usefulInformation = "";
            int count = 0;
            String str;
            while ((str = bufr.readLine()) != null) {
                if (find && count<=15){
                    usefulInformation += str;
                    count++;
                }
                if(str.contains("<div class=\"image\">")){
                    find = true;
                }
            }
            bufr.close();
            isr.close();
            in.close();
            //get the poster url
            Pattern pattern = Pattern.compile("src=\"(.+?)\"");
            Matcher matcher = pattern.matcher(usefulInformation);
            while (matcher.find()){
                posterUrl =  matcher.group(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return posterUrl;
    }

}
