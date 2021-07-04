package cn.lovecamille.fakertomcat.util;

import cn.hutool.system.SystemUtil;

import java.io.File;

/**
 * @author wuyan
 */
public class Constant {

    public final static String RESPONSE_HEAD_202 =
            """
                    HTTP/1.1 200 OK\r
                    Content-Type: {}\r
                    \r
                    """;

    public static final String RESPONSE_HEAD_404 =
            """
                    HTTP/1.1 404 Not Found\r
                    Content-Type: text/html\r
                    \r
                    """;

    public static final String RESPONSE_HEAD_500 =
            """
                    HTTP/1.1 500 Internal Server Error\r
                    Content-Type:text/html\r
                    \r
                    """;

    public static final String TEXT_FORMAT_404 =
            "<html><head><title>Faker Tomcat/1.0.1 - Error report</title><style>" +
                    "<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} " +
                    "H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} " +
                    "H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} " +
                    "BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} " +
                    "B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} " +
                    "P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}" +
                    "A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> " +
                    "</head><body><h1>HTTP Status 404 - {}</h1>" +
                    "<HR size='1' noshade='noshade'><p><b>type</b> Status report</p><p><b>message</b> <u>{}</u></p><p><b>description</b> " +
                    "<u>The requested resource is not available.</u></p><HR size='1' noshade='noshade'><h3>FakerTomcat 1.0.1</h3>" +
                    "</body></html>";

    public static final String TEXT_FORMAT_500 =
            "<html><head><title>Faker Tomcat/1.0.1 - Error report</title><style>"
                    + "<!--H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} "
                    + "H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} "
                    + "H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} "
                    + "BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} "
                    + "B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} "
                    + "P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}"
                    + "A {color : black;}A.name {color : black;}HR {color : #525D76;}--></style> "
                    + "</head><body><h1>HTTP Status 500 - An exception occurred processing {}</h1>"
                    + "<HR size='1' noshade='noshade'><p><b>type</b> Exception report</p><p><b>message</b> <u>An exception occurred processing {}</u></p><p><b>description</b> "
                    + "<u>The server encountered an internal error that prevented it from fulfilling this request.</u></p>"
                    + "<p>Stacktrace:</p>" + "<pre>{}</pre>" + "<HR size='1' noshade='noshade'><h3>FakerTomcat 1.0.1</h3>"
                    + "</body></html>";

    public final static File webappsFolder = new File(SystemUtil.get("user.dir"), "webapps");
    public final static File rootFolder =  new File(webappsFolder, "ROOT");

    public final static File confFolder = new File(SystemUtil.get("user.dir"), "conf");
    public final static File serverXmlFile = new File(confFolder, "server.xml");

    public static final File webXmlFile = new File(confFolder, "web.xml");
}
