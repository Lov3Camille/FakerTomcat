package cn.lovecamille.fakertomcat.web;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;

/**
 * @author wuyan
 */
public class HttpResponse extends BaseResponse {

    private StringWriter stringWriter;
    private PrintWriter writer;
    private String contentType;
    private byte[] body;
    private int status;

    public HttpResponse() {
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    public byte[] getBody() {
        if (null == body) {
            String content = stringWriter.toString();
            return content.getBytes(StandardCharsets.UTF_8);
        }
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
