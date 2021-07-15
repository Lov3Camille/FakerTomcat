package cn.lovecamille.fakertomcat.util;

import cn.hutool.core.io.FileUtil;
import java.lang.annotation.Documented;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ContextXmlUtil {

    public static String getWatchedResource() {
        try {
            String xml = FileUtil.readUtf8String(Constant.contextXmlFile);
            Document document = Jsoup.parse(xml);
            Element element = document.select("WatchedResource").first();
            return element.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "WEB-INF/web.xml";
        }
    }
}
