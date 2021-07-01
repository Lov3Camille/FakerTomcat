package cn.lovecamille.fakertomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import cn.lovecamille.fakertomcat.util.Constant;
import cn.lovecamille.fakertomcat.util.ThreadPoolUtil;
import cn.lovecamille.fakertomcat.web.HttpRequest;
import cn.lovecamille.fakertomcat.web.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wuyan
 */
public class Server {
    private Service service;
    public Server() {
        this.service = new Service(this);
    }

    public void start() {
        logJVM();
        init();
    }

    private void init() {
        try {
            int port = 18080;

            // build a server at port 18080
            ServerSocket ss = new ServerSocket(port);

            while (true) {

                // listen to the serve and accept the connection
                Socket s = ss.accept();
                Runnable runnable = () -> {
                    try {
                        HttpRequest httpRequest = new HttpRequest(s, service);
                        HttpResponse httpResponse = new HttpResponse();
                        String uri = httpRequest.getUri();
                        if (null == uri) {
                            return;
                        }
                        System.out.println("Browser's input is: \r\n" + httpRequest.getRequestString());
                        System.out.println("URI is: " + uri);

                        Context context = httpRequest.getContext();

                        if ("/".equals(uri)) {
                            String html = "Hello FakerTomcat from WU Yan";
                            httpResponse.getWriter().println(html);
                        } else {
                            String fileName = StrUtil.removePrefix(uri, "/");
                            File file = FileUtil.file(context.getDocBase(), fileName);
                            if (file.exists()) {
                                String fileContents = FileUtil.readUtf8String(file);
                                httpResponse.getWriter().println(fileContents);

                                if ("timeConsumer.html".equals(fileName)) {
                                    ThreadUtil.sleep(1000);
                                }
                            } else {
                                httpResponse.getWriter().println("File Not Found");
                            }
                        }
                        handle200(s, httpResponse);
                    } catch (IOException e) {
                        LogFactory.get().error(e);
                        e.printStackTrace();
                    }
                };
                ThreadPoolUtil.run(runnable);
            }
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }

    /**
     * show basic information of JRE
     */
    private static void logJVM() {
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server Version", "FakerTomcat/1.0.0");
        infos.put("Server built", "2021-06-27 17:10:47");
        infos.put("Server number", "1.0.0");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

        Set<String> keys = infos.keySet();
        for (String key : keys) {
            LogFactory.get().info(key + ":\t\t" + infos.get(key));
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
}
