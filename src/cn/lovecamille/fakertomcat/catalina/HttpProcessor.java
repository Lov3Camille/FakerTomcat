package cn.lovecamille.fakertomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.lovecamille.fakertomcat.util.Constant;
import cn.lovecamille.fakertomcat.util.WebXmlUtil;
import cn.lovecamille.fakertomcat.web.HttpRequest;
import cn.lovecamille.fakertomcat.web.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author wuyan
 */
public class HttpProcessor {
    public void execute(Socket socket, HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String uri = httpRequest.getUri();
            if (null == uri) {
                return;
            }
            System.out.println("Browser's input is: \r\n" + httpRequest.getRequestString());
            System.out.println("URI is: " + uri);

            Context context = httpRequest.getContext();

            if ("/500.html".equals(uri)) {
                throw new Exception("this is a deliberately created exception");
            }
            if ("/".equals(uri)) {
                uri = WebXmlUtil.getWelcomeFile(httpRequest.getContext());
            }

            String fileName = StrUtil.removePrefix(uri, "/");
            File file = FileUtil.file(context.getDocBase(), fileName);
            if (file.exists()) {
                String extName = FileUtil.extName(file);
                String mimeType = WebXmlUtil.getMimeType(extName);
                httpResponse.setContentType(mimeType);

                byte[] body = FileUtil.readBytes(file);
                httpResponse.setBody(body);

                if ("timeConsumer.html".equals(fileName)) {
                    ThreadUtil.sleep(1000);
                }
            } else {
                handle404(socket, uri);
                return;
            }
            handle200(socket, httpResponse);
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * parse the http response if it is "200 OK"
     * @param s socket
     * @param httpResponse response
     * @throws IOException IOException
     */
    private static void handle200(Socket s, HttpResponse httpResponse) throws IOException {
        String contentType = httpResponse.getContentType();
        String headText = Constant.RESPONSE_HEAD_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();

        byte[] body = httpResponse.getBody();

        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        s.close();
    }

    protected void handle404(Socket s, String uri) throws IOException {
        OutputStream os = s.getOutputStream();
        String responseText = StrUtil.format(Constant.TEXT_FORMAT_404, uri, uri);
        responseText = Constant.RESPONSE_HEAD_404 + responseText;
        byte[] responseByte = responseText.getBytes(StandardCharsets.UTF_8);
        os.write(responseByte);
    }

    protected void handle500(Socket s, Exception e) {
        try {
            OutputStream os = s.getOutputStream();
            StackTraceElement[] stackTraceElement = e.getStackTrace();
            StringBuffer sb = new StringBuffer();
            sb.append(e.toString());
            sb.append("\r\n");
            for (StackTraceElement ste : stackTraceElement) {
                sb.append("\t");
                sb.append(ste.toString());
                sb.append("\r\n");
            }

            String msg = e.getMessage();

            if (null != msg && msg.length() > 0) {
                msg = msg.substring(0, 19);
            }

            String text = StrUtil.format(Constant.TEXT_FORMAT_500, msg, e.toString(), sb.toString());
            text = Constant.RESPONSE_HEAD_500 + text;
            byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
            os.write(responseBytes);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
