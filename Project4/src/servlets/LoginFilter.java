package servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // Check if this URL is allowed to access without logging in for customer
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        //check if it is the manager access
        String isLoginForManager = (String) httpRequest.getSession().getAttribute("manlogin");
        if(this.isUrlWantToManage(httpRequest.getRequestURI())){
            if(isLoginForManager == null || isLoginForManager.equals("false")){
                httpResponse.sendRedirect("managerlogin.html");
                return;
            }else {
                chain.doFilter(request, response);
                return;
            }
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        String isLogin = (String) httpRequest.getSession().getAttribute("isLogin");
        if ((isLogin == null || isLogin.equals("false"))) {
            httpResponse.sendRedirect("login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    // Setup your own rules here to allow accessing some resources without logging in
    // Always allow your own login related requests(html, js, servlet, etc..)
    // You might also want to allow some CSS files, etc..
    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        requestURI = requestURI.toLowerCase();

        return requestURI.endsWith("login.html") || requestURI.endsWith("login.js")
                || requestURI.endsWith("/login") || requestURI.endsWith("jquery.js")
                || requestURI.endsWith("login.css") || requestURI.endsWith("style.min.css")
                || requestURI.endsWith("icon.svg") || requestURI.endsWith("managerlogin.html")
                || requestURI.endsWith("manlogin.js") || requestURI.endsWith("/managerlogin")
                || requestURI.endsWith("/android-login");
    }

    private boolean isUrlWantToManage(String requestURI) {
        requestURI = requestURI.toLowerCase();

        return requestURI.endsWith("dashboard.html") || requestURI.endsWith("addmovie.html")
                || requestURI.endsWith("insertstar.js") || requestURI.endsWith("manlogout.js")
                || requestURI.endsWith("insertmoviedetail.js") || requestURI.endsWith("/addmovie")
                || requestURI.endsWith("/addstar") || requestURI.endsWith("/addmovie")
                || requestURI.endsWith("/managerlogout");
    }

    /**
     * We need to have these function because this class implements Filter.
     * But we don’t need to put any code in them.
     *
     * @see Filter#init(FilterConfig)
     */

    public void init(FilterConfig fConfig) {
    }

    public void destroy() {
    }


}