package cn.lovecamille.fakertomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.lovecamille.fakertomcat.classloader.WebappClassLoader;
import cn.lovecamille.fakertomcat.exception.WebConfigDuplicateException;
import cn.lovecamille.fakertomcat.util.ContextXmlUtil;
import cn.lovecamille.fakertomcat.watcher.ContextFileChangeWatcher;
import cn.lovecamille.fakertomcat.web.ApplicationContext;
import cn.lovecamille.fakertomcat.web.StandardServletConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * prototype of webapps
 * @author wuyan
 */
public class Context {

    private String path;
    private String docBase;
    private File contextWebXmlFile;

    private Map<String, String> url_servletClassName;
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;
    private Map<String, Map<String, String>> servlet_className_init_params;

    private WebappClassLoader webappClassLoader;

    private Host host;
    private boolean reloadable;
    private ContextFileChangeWatcher contextFileChangeWatcher;
    private ServletContext servletContext;
    private Map<Class<?>, HttpServlet> servletPool;

    public Context(String path, String docBase, Host host, boolean reloadable) {
        TimeInterval timeInterval = DateUtil.timer();
        this.host = host;
        this.reloadable = reloadable;

        this.path = path;
        this.docBase = docBase;
        this.contextWebXmlFile = new File(docBase, ContextXmlUtil.getWatchedResource());

        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();
        this.servlet_className_init_params = new HashMap<>();

        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(docBase, commonClassLoader);

        this.servletContext = new ApplicationContext(this);
        this.servletPool = new HashMap<>();

        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        deploy();
        LogFactory.get().info("Deploying web application directory {} has finished in {} ms", this.docBase, timeInterval.intervalMs());
    }

    public void reload() {
        host.reload(this);
    }

    private void deploy() {
        TimeInterval timeInterval = DateUtil.timer();
        init();

        if (reloadable) {
            contextFileChangeWatcher = new ContextFileChangeWatcher(this);
            contextFileChangeWatcher.start();
        }
    }

    private void init() {
        if (!contextWebXmlFile.exists()) {
            return;
        }
         try {
            checkDuplicated();
         } catch (WebConfigDuplicateException e) {
             e.printStackTrace();
             return;
         }

         String xml = FileUtil.readUtf8String(contextWebXmlFile);
         Document document = Jsoup.parse(xml);
         parseServletMapping(document);
         parseServletInitParams(document);
    }

    private void parseServletInitParams(Document document) {
        Elements servletClassNameElements = document.select("servlet-class");
        for (Element servletClassNameElement : servletClassNameElements) {
            String servletClassName = servletClassNameElement.text();

            Elements initElements = servletClassNameElement.parent().select("init-param");
            if (initElements.isEmpty()) {
                continue;
            }

            Map<String, String> initParams = new HashMap<>();

            for (Element element : initElements) {
                String name = element.select("param-name").get(0).text();
                String value = element.select("param-value").get(0).text();
                initParams.put(name, value);
            }

            servlet_className_init_params.put(servletClassName, initParams);
        }
    }

    private void parseServletMapping(Document document) {
        Elements mappingUrlElements = document.select("servlet-mapping url-pattern");
        for (Element element : mappingUrlElements) {
            String urlPattern = element.text();
            String servletName = element.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern, servletName);
        }

        Elements servletNameElements = document.select("servlet servlet-name");
        for (Element element : servletNameElements) {
            String servletName  = element.text();
            String servletClass = element.parent().select("servlet-class").first().text();
            servletName_className.put(servletName, servletClass);
            className_servletName.put(servletClass, servletName);
        }

        Set<String> urls = url_servletName.keySet();
        for (String url : urls) {
            String servletName = url_servletName.get(url);
            String servletClassName = servletName_className.get(servletName);
            url_servletClassName.put(url, servletClassName);
        }
    }

    private void destroyServlets() {
        Collection<HttpServlet> servlets = servletPool.values();
        for (HttpServlet servlet : servlets) {
            servlet.destroy();
        }
    }

    private void checkDuplicated(Document document, String mapping, String desc) throws WebConfigDuplicateException {
        Elements elements = document.select(mapping);

        List<String> contents = new ArrayList<>();
        for (Element element : elements) {
            contents.add(element.text());
        }
        Collections.sort(contents);
        // drop into a collection and judge whether the neighbor are equal to each other
        for (int i = 0; i < contents.size() - 1; i++) {
            String contentPre = contents.get(i);
            String contentNext = contents.get(i + 1);
            if (contentPre.equals(contentNext)) {
                throw new WebConfigDuplicateException(StrUtil.format(desc, contentPre));
            }
        }
    }

    private void checkDuplicated() throws WebConfigDuplicateException {
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document document = Jsoup.parse(xml);

        checkDuplicated(document, "servlet-mapping url-pattern", "servlet url duplicates, please keep it unique:{} ");
        checkDuplicated(document, "servlet servlet-name", "servlet's name duplicates, please keep it unique:{}");
        checkDuplicated(document, "servlet servlet-class", "servlet's class name duplicates, please keep it unique:{}");
    }

    public synchronized HttpServlet getServlet(Class<?> clazz) {
        HttpServlet servlet = servletPool.get(clazz);
        if (null == servlet) {
            try {
                servlet = (HttpServlet) clazz.newInstance();
                ServletContext servletContext = this.getServletContext();

                String className = clazz.getName();
                String servletName = className_servletName.get(className);

                Map<String, String> initParameters = servlet_className_init_params.get(className);
                ServletConfig servletConfig = new StandardServletConfig(servletContext, servletName, initParameters);

                servlet.init(servletConfig);
                servletPool.put(clazz, servlet);
            } catch (InstantiationException | IllegalAccessException | ServletException e) {
                e.printStackTrace();
            }
        }
        return servlet;
    }

    public String getServletClassName(String uri) {
        return url_servletClassName.get(uri);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public WebappClassLoader getWebappClassLoader() {
        return webappClassLoader;
    }

    public void stop() {
        webappClassLoader.stop();
        contextFileChangeWatcher.stop();

        destroyServlets();
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
