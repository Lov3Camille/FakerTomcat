package cn.lovecamille.fakertomcat.web;

import cn.hutool.core.util.StrUtil;
import cn.lovecamille.fakertomcat.Bootstrap;
import cn.lovecamille.fakertomcat.catalina.Context;
import cn.lovecamille.fakertomcat.catalina.Engine;
import cn.lovecamille.fakertomcat.catalina.Host;
import cn.lovecamille.fakertomcat.catalina.Service;
import cn.lovecamille.fakertomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyan
 */
public class HttpRequest {

    private String requestString;
    private String uri;
    private final Socket socket;
    private Context context;
    private Service service;
    private final char URL_SEPERATOR = '?';

    public HttpRequest(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)) {
            return;
        }
        parseUri();
        parseContext();

        if (!"/".equals(context.getPath())) {
            uri = StrUtil.removePrefix(uri, context.getPath());
            if (StrUtil.isEmpty(uri)) {
                uri = "/";
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * parse context's relative path
     */
    private void parseContext() {
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContext(uri);
        if (null != context) {
            return;
        }

        String path = StrUtil.subBetween(uri, "/", "/");
        if (null == path) {
            path = "/";
        } else {
            path = "/" + path;
        }

        context = engine.getDefaultHost().getContext(path);

        if (null == context) {
            context = engine.getDefaultHost().getContext("/");
        }
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is, false);
        requestString = new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * get the request parameter
     */
    private void parseUri() {
        String temp;

        temp = StrUtil.subBetween(requestString, " ", " ");
        if (!StrUtil.contains(temp, URL_SEPERATOR)) {
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp, URL_SEPERATOR, false);
        uri = temp;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestString() {
        return requestString;
    }
}
