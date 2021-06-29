package cn.lovecamille.fakertomcat.web;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyan
 */
public class HttpResponse {

    private StringWriter stringWriter;
    private PrintWriter writer;
    private String contentType;

    public HttpResponse() {
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    public String getContentType() {
        return contentType;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public byte[] getBody() throws UnsupportedEncodingException {
        String content = stringWriter.toString();
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
