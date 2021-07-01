package cn.lovecamille.fakertomcat;

import cn.lovecamille.fakertomcat.catalina.Server;

/**
 * @author wuyan
 */
public class Bootstrap {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
