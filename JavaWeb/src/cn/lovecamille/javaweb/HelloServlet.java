package cn.lovecamille.javaweb;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {
    public HelloServlet() {
        System.out.println(this + " 's constructor");
    }

    public void init(ServletConfig config) {
        String author = config.getInitParameter("author");
        String site = config.getInitParameter("site");

        System.out.println(this + " 's initiate method");
        System.out.println("get the parameter author : " + author);
        System.out.println("get the parameter site : " + site);
    }

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            System.out.println(this + " 's doGet() method");
            httpServletResponse.getWriter().println("Hello FakerTomcat from HelloServlet@javaweb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        System.out.println(this + " has been destroyed");
    }
}
