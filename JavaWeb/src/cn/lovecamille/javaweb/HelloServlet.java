package cn.lovecamille.javaweb;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {
    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            httpServletResponse.getWriter().println("Hello FakerTomcat from HelloServlet@JavaWeb");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
