package cn.lovecamille.fakertomcat.catalina;

import cn.lovecamille.fakertomcat.util.ServerXmlUtil;

import java.util.List;

/**
 * @author wuyan
 */
public class Engine {
    private String defaultHost;
    private List<Host> hosts;

    public Engine() {
        this.defaultHost = ServerXmlUtil.getEngineDefaultHost();
        this.hosts = ServerXmlUtil.getHosts(this);
        checkDefault();
    }

    private void checkDefault() {
        if (null == getDefaultHost()) {
            throw new RuntimeException("the defaultHost: " + defaultHost + " does not exist!");
        }
    }

    public Host getDefaultHost() {
        for (Host host : hosts) {
            if (host.getName().equals(defaultHost)) {
                return host;
            }
        }
        return null;
    }
}
