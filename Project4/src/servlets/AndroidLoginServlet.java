package servlets;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/*
 * We create a separate android login Servlet here because
 *   the recaptcha secret key for web and android are different.
 *
 */
@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AndroidLoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        //

        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(key);
            System.out.println(map.get(key)[0]);
        }

        // then verify username / password

        JsonObject loginResult = new JsonObject();

        if(username==null||password==null)
        {
            loginResult.addProperty("status", "fail");
            response.getWriter().write(loginResult.toString());
        }


        boolean success = false;
        try {
            success = VerifyPassword.verifyCredentials(username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //success = true;
        loginResult.addProperty("status", success?"success":"fail");

        if (loginResult.get("status").getAsString().equals("success")) {
            // login success
            request.getSession().setAttribute("isLogin","true");
            request.getSession().setAttribute("username",username);
            response.getWriter().write(loginResult.toString());
        } else {
            response.getWriter().write(loginResult.toString());
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

}