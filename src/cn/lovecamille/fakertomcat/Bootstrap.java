package cn.lovecamille.fakertomcat;

import cn.lovecamille.fakertomcat.catalina.Server;
import cn.lovecamille.fakertomcat.classloader.CommonClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wuyan
 */
public class Bootstrap {
    public static void main(String[] args) {
        CommonClassLoader commonClassLoader = new CommonClassLoader();
        Thread.currentThread().setContextClassLoader(commonClassLoader);
        String serverClassName = "cn.lovecamille.fakertomcat.catalina.Server";
        try {
            Class<?> serverClazz = commonClassLoader.loadClass(serverClassName);
            try {
                Object serverObject = serverClazz.newInstance();
                Method method = serverClazz.getMethod("start");
                method.invoke(serverObject);
                System.out.println(serverClazz.getClassLoader());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
