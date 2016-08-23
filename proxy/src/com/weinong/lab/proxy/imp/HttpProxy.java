package com.weinong.lab.proxy.imp;

import com.weinong.lab.proxy.IProxy;

import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/8/18.
 */
public class HttpProxy implements IProxy{

    private static final String[] HTTP_METHODS = new String[]{"OPTIONS", "GET", "POST", "CONNECT", "PUT", "DELETE", "TRACE", "HEAD"};

    private Logger logger = Logger.getLogger(HttpProxy.class.getName());

    private boolean is_ready_to_translate_data;

    private Status proxy_status = Status.HAND_SHAKE;

    /**是否是connect的请求方式，这种方式属于代理模式*/
    private boolean is_connect_method;

    /**
     * 代理处于的状态，握手状态，一开始的时候就是在HAND_SHAKE, 然后是处于 AUTHORIZATION的用户权限验证， 处于ALL_READY是可以直接进行数据转发的环节
     */
    enum Status{HAND_SHAKE, AUTHORIZATION, ALL_READY }

    @Override
    public boolean isReadlyToTranslateData() {
        return is_ready_to_translate_data;
    }

    @Override
    public byte[] getLocalResponse(byte[] data, int start_point, int end_point) {

        //如果是第一次请求，解析数据
        if (proxy_status == Status.HAND_SHAKE) {

        }
        return new byte[0];
    }

    @Override
    public byte[] getRemoteRequest(byte[] data, int start_point, int end_point) {
        return new byte[0];
    }


    public static void main(String[] args) {
        System.out.println("kjdljflaj\nljljsljdfljl");
        System.out.println("kjdljflaj\r\nljljsljdfljl");
        System.out.println("kjdljflaj\nljljsljdfljl");
        System.out.println("kjdljflaj\nljljsljdfljl");
    }
}
