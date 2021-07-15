package cn.lovecamille.fakertomcat.servlets;

import cn.hutool.core.util.ReflectUtil;
import cn.lovecamille.fakertomcat.catalina.Context;
import cn.lovecamille.fakertomcat.util.Constant;
import cn.lovecamille.fakertomcat.web.HttpRequest;
import cn.lovecamille.fakertomcat.web.HttpResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InvokerServlet extends HttpServlet {
    private static InvokerServlet instance = new InvokerServlet();

    public static synchronized InvokerServlet getInstance() {
        return instance;
    }

    private InvokerServlet() {

    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws IOException, ServletException {
        HttpRequest request = (HttpRequest) httpServletRequest;
        HttpResponse response = (HttpResponse) httpServletResponse;

        String uri = request.getUri();
        Context context = request.getContext();
        String servletClassName = context.getServletClassName(uri);

        try {
            Class servletClass = context.getWebappClassLoader().loadClass(servletClassName);
            Object servletObject = context.getServlet(servletClass);
            ReflectUtil.invoke(servletObject, "service", request, response);
            response.setStatus(Constant.CODE_200);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
