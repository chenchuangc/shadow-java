package com.weinong.lab.proxy;

/**
 * Created by Administrator on 2016/8/18.
 */
public interface IProxy {

    boolean isReadlyToTranslateData();

    enum TYPE {HTTP, SOCKS5, AUTO}


    byte[] getLocalResponse(byte[] data, int start_point, int end_point);

    byte[] getRemoteRequest(byte[] data, int start_point, int end_point);


}
