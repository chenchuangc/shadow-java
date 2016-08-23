package com.weinong.lab.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weinong.lab.proxy.IProxy;
import com.weinong.lab.proxy.ProxyFactory;

import java.net.URL;

/**
 * Created by Administrator on 2016/8/18.
 */
public class Config {

    private static String _ipAddr;
    private static int _port;
    private static String _localIpAddr;
    private static int _localPort;
    private static String _method;
    private static String _password;
    private static String _logLevel;
    private static IProxy.TYPE _proxyType;

    /**
     * json配置存放地点
     */
    private static JSONObject config;

    public static void init() {

        URL url = Config.class.getClassLoader().getResource("shadow_config.json");
        System.out.println(url.toString());
//        url.getPath();
//        Path pa = Paths.get(url);
        String content = FileUtil.getFileContent(url.getPath());
        config = JSON.parseObject(content);

        initParms();

    }

    private static void initParms() {

        _ipAddr = config.getString("remote_ip_address");
        _port = Integer.parseInt(config.getString("remote_port"));
        _localIpAddr = config.getString("local_ip_address");
        _localPort = Integer.parseInt(config.getString("local_port"));
        _method = config.getString("method");
        _password = config.getString("password");
        setLogLevel(config.getString("log_level"));
        setProxyType(config.getString("proxy_type"));


    }

    /**
     * 获取不同类型的配置信息
     * @param key
     * @param target_type
     * @param <T>
     * @return
     */
    public static <T> T getParam(String key, Class<T> target_type) {

        return (T)config.get(key);
    }


    /**
     *  获取配置的json
     * @param key
     * @return
     */
    public static String getStringParm(String key) {
        return (String) config.get(key);
    }


    public static void main(String[] args) {
        init();
    }

    /**
     * 通知ProxyFactory
     * @param proxy_type
     */
    public static void setProxyType(String proxy_type){
        _proxyType = IProxy.TYPE.valueOf(proxy_type);
        ProxyFactory.setProxy_type(_proxyType);

    }

    public static void setLogLevel(String logLevel) {

    }
}
