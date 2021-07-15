package cn.lovecamille.fakertomcat.util;

import static cn.lovecamille.fakertomcat.util.Constant.webXmlFile;

import cn.hutool.core.io.FileUtil;
import cn.lovecamille.fakertomcat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyan
 */
public class WebXmlUtil {
    private static Map<String, String> mimeTypeMapping = new HashMap<>();

    public static synchronized String getMimeType(String extName) {
        if (mimeTypeMapping.isEmpty()) {
            initMimeType();
        }
        String mimeType = mimeTypeMapping.get(extName);
        if (null == mimeType) {
            return "text/html";
        }
        return mimeType;
    }

    private static void initMimeType() {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document document = Jsoup.parse(xml);

        Elements elements = document.select("mime-mapping");
        for (Element element : elements) {
            String extName = element.select("extension").first().text();
            String mimeType = element.select("mime-type").first().text();
            mimeTypeMapping.put(extName, mimeType);
        }
    }

    public static String getWelcomeFile(Context context) {
        String xml = FileUtil.readUtf8String(webXmlFile);
        Document document = Jsoup.parse(xml);
        Elements elements = document.select("welcome-file");
        for (Element element : elements) {
            String welcomeFileName = element.text();
            File file = new File(context.getDocBase(), welcomeFileName);
            if (file.exists()) {
                return file.getName();
            }
        }
        return "index.html";
    }
}
