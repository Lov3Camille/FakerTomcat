package cn.lovecamille.fakertomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import cn.lovecamille.fakertomcat.util.ServerXmlUtil;

import java.util.List;

/**
 * @author wuyan
 */
public class Service {
    private String name;
    private Engine engine;
    private Server server;

    private List<Connector> connectors;
    public Service(Server server) {
        this.server = server;
        this.name = ServerXmlUtil.getServiceName();
        this.engine = new Engine(this);
        this.connectors = ServerXmlUtil.getConnectors(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public Server getServer() {
        return server;
    }

    public void start() {
        init();
    }

    private void init() {
        TimeInterval timeInterval = DateUtil.timer();
        for (Connector connector : connectors) {
            connector.init();
        }
        LogFactory.get().info("Initialization processed in {} ms", timeInterval.intervalMs());
        for (Connector connector : connectors) {
            connector.start();
        }
    }
}
