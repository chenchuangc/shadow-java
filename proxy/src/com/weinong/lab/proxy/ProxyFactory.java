package com.weinong.lab.proxy;

import com.weinong.lab.proxy.imp.AutoProxy;
import com.weinong.lab.proxy.imp.HttpProxy;
import com.weinong.lab.proxy.imp.Socks5Proxy;
import com.weinong.lab.util.ReflectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/18.
 */
public class ProxyFactory {

    private static IProxy.TYPE proxy_type;

    private static final Map<IProxy.TYPE, String> proxy_map = new HashMap<IProxy.TYPE, String>(){
        {

            put(IProxy.TYPE.HTTP, HttpProxy.class.getName());
            put(IProxy.TYPE.SOCKS5, Socks5Proxy.class.getName());
            put(IProxy.TYPE.AUTO, AutoProxy.class.getName());
        }
    };


    /**
     * 根据class 创建类
     * @param target_class
     * @return
     */
    public static IProxy createProxy(Class target_class) {
        return createProxy(target_class.getClass().getName());
    }

    /**
     * 根据类名来实例化代理
     * @param name
     * @return
     */
    public static IProxy createProxy(String name) {

        return (IProxy) ReflectUtil.getInstance(proxy_map.get(IProxy.TYPE.valueOf(name)));
    }

    /**
     * 根据配置文件的配置信息类创建类
     * @return
     */
    public  static IProxy createProxyByConfig() {
        return (IProxy) ReflectUtil.getInstance(proxy_map.get(proxy_type));
    }


    public static void setProxy_type(IProxy.TYPE proxy_type) {
        ProxyFactory.proxy_type = proxy_type;
    }
}
