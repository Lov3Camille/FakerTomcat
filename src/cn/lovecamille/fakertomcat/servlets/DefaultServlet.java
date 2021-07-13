package cn.lovecamille.fakertomcat.servlets;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.lovecamille.fakertomcat.catalina.Context;
import cn.lovecamille.fakertomcat.util.Constant;
import cn.lovecamille.fakertomcat.util.WebXmlUtil;
import cn.lovecamille.fakertomcat.web.HttpRequest;
import cn.lovecamille.fakertomcat.web.HttpResponse;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultServlet extends HttpServlet {
    private static DefaultServlet instance = new DefaultServlet();

    public static synchronized DefaultServlet getInstance() {
        return instance;
    }

    private DefaultServlet() {

    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        HttpRequest request = (HttpRequest) httpServletRequest;
        HttpResponse response = (HttpResponse) httpServletResponse;

        Context context = request.getContext();

        String uri = request.getUri();

        if ("/500.html".equals(uri)) {
            throw new RuntimeException("this is a deliberately created exception");
        }

        if ("/".equals(uri)) {
            uri = WebXmlUtil.getWelcomeFile(request.getContext());
        }

        String fileName = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(context.getDocBase(), fileName);

        if (file.exists()) {
            String extName = FileUtil.extName(file);
            String mimeType = WebXmlUtil.getMimeType(extName);
            response.setContentType(mimeType);

            byte[] body = FileUtil.readBytes(file);
            response.setBody(body);

            if (fileName.equals("timeConsume.html")) {
                ThreadUtil.sleep(1000);
            }
            response.setStatus(Constant.CODE_200);
        } else {
            response.setStatus(Constant.CODE_404);
        }
    }
}
