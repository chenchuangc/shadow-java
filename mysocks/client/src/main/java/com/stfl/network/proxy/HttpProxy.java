/*
 * Copyright (c) 2015, Blake
 * All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote
 * products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.stfl.network.proxy;

import com.stfl.misc.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provide local HTTP proxy statue and required response
 */

/**
 * http代理，对于connect的方式则是建立连接，直接转发
 */
public class HttpProxy implements IProxy {
    private static final String[] HTTP_METHODS =
            new String[] {"OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "CONNECT"};

    private Logger logger = Logger.getLogger(HttpProxy.class.getName());
    private boolean _isReady;
    private boolean _isHttpConnect;//这个是用来标识是否为connect的请求方法，这种请求方法只是直接建立连接，然后直接转发请求内容就可以了
    private Map<String, String> methodCache;

    public HttpProxy() {
        _isReady = false;
        _isHttpConnect = false;
    }

    public TYPE getType() {
        return TYPE.HTTP;
    }

    public boolean isReady() {
        return _isReady;
    }

    /**
     * 对于connect方式的直接给返回代理服务器的信息，
     * @param data
     * @return
     */
    public byte[] getResponse(byte[] data) {
        if (methodCache == null) {
            methodCache = getHttpMethod(data);
        }
        setHttpMethod(methodCache);//设置请求方式标志位

        if (_isHttpConnect)
            return "HTTP/1.0 200 Connection Established\r\n\r\n".getBytes();

        return null;
    }

    /**
     * 获取要请求目标机的 host 和 port，port的默认值为80端口，同时将服务器设置成已经准备好的状态（可以和远端交互的状态）
     * @param data
     * @return
     */
    public List<byte[]> getRemoteResponse(byte[] data) {
        List<byte[]> respData = new ArrayList<>(2);
        String host;
        int port = 80; // HTTP port
        if (methodCache == null) {
            methodCache = getHttpMethod(data);
        }
        String[] hostInfo = methodCache.get("host").split(":");

        // get hostname and port
        host = hostInfo[0];
        if (hostInfo.length > 1) {
            port = Integer.parseInt(hostInfo[1]);
        }

        byte[] ssHeader = Util.composeSSHeader(host, port);

        //####################################################################
        /*byte[] pass_header = new byte[ssHeader.length + 18];
        System.arraycopy(ssHeader, 0, pass_header, 0, ssHeader.length);
        pass_header[ssHeader.length]=0x01;
        pass_header[ssHeader.length+1]=0x01;
        try {
            System.arraycopy(ShadowSocksKey.getMd5("aaa"),0,pass_header,ssHeader.length+2,16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ssHeader = pass_header;*/
        //####################################################################
        respData.add(ssHeader);
        if (!_isHttpConnect) { //如果不是connect连接方式的，那么还要将请求数据全部提取出来，发送给远端
            byte[] httpHeader = reconstructHttpHeader(methodCache, data);
            respData.add(httpHeader);
        }

        _isReady = true;
        return respData;
    }

    /**
     * 是否是支持的请求方式
     * @param data
     * @return
     */
    @Override
    public boolean isMine(byte[] data) {
        if (methodCache == null) {
            methodCache = getHttpMethod(data);
        }
        String method = methodCache.get("method");

        if (method != null) {
            for (String s : HTTP_METHODS) {
                if (s.equals(method)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 获取 method, url , host, url, version参数
     * @param data
     * @return
     */
    private Map<String, String> getHttpMethod(byte[] data) {
        String httpRequest = Util.bytesToString(data, 0, data.length);
        String[] httpHeaders = httpRequest.split("\\r?\\n");
        boolean isHostFound = true;
        //Pattern pattern = Pattern.compile("^([a-zA-Z]*) [hHtTpP]{0,4}[:\\/]{0,3}(\\S[^/ ]*)");
        Pattern pattern = Pattern.compile("^([a-zA-Z]*) [htps]{0,5}[:/]{0,3}(\\S[^/]*)(\\S*) (\\S*)");
        Map<String, String> header = new HashMap<>();
        if (httpHeaders.length > 0) {
            logger.fine("HTTP Header: " + httpHeaders[0]);
            Matcher matcher = pattern.matcher(httpHeaders[0]);
            if (matcher.find()) {
                header.put("method", matcher.group(1));
                if (matcher.group(2).startsWith("/")) {
                    header.put("url", "/");
                    isHostFound = false;
                }
                else {
                    header.put("host", matcher.group(2));
                    header.put("url", matcher.group(3));
                }
                header.put("version", matcher.group(4));
            }
        }

        if (!isHostFound) {
            for (String line : httpHeaders) {
                if (line.toLowerCase().contains("host")) {
                    String info = line.split(":")[1].trim();
                    header.put("host", info);
                    break;
                }
            }
        }
        return header;
    }

    private byte[] reconstructHttpHeader(Map<String, String> method, byte[] data) {
        String httpRequest = Util.bytesToString(data, 0, data.length);
        String[] httpHeaders = httpRequest.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        boolean isFirstLine = true;

        //logger.info("original HttpHeader:" + httpRequest);
        for (String line : httpHeaders) {
            if (isFirstLine && _isHttpConnect) {
                sb.append(method.get("method"));
                sb.append(" ");
                sb.append(method.get("host"));
                sb.append(" ");
                sb.append(method.get("version"));
                sb.append("\r\n");
                sb.append("User-Agent: test/0.1\r\n");
                break;
            }
            else if (isFirstLine) {
                sb.append(method.get("method"));
                sb.append(" ");
                sb.append("http://");
                sb.append(method.get("host"));
                sb.append(method.get("url"));
                sb.append(" ");
                sb.append(method.get("version"));
                isFirstLine = false;
            }
            else if (line.toLowerCase().contains("cache-control")) {
                sb.append("Pragma: no-cache\r\n");
                sb.append("Cache-Control: no-cache");
            }
            else if (line.toLowerCase().contains("proxy-connection")) {
                //Proxy-Connection
                String[] fields = line.split(":");
                sb.append("Connection: ");
                sb.append(fields[1].trim());
            }
            else if (line.toLowerCase().contains("if-none-match")) {
                continue;
            }
            else if (line.toLowerCase().contains("if-modified-since")) {
                continue;
            }
            else {
                sb.append(line);
            }
            sb.append("\r\n");
        }

        sb.append("\r\n");
        logger.info("reconstructHttpHeader:" + sb.toString());
        return sb.toString().getBytes();
    }

    private void setHttpMethod(Map<String, String> header) {
        String method = header.get("method");

        if (method != null) {
            if (method.toUpperCase().equals("CONNECT")) {
                _isHttpConnect = true;
            }
            else {
                _isHttpConnect = false;
            }
        }
    }


    public static void main(String[] args) {
        String src = "GET http://www.cnblogs.com/mvc/Blog/GetBlocks.aspx?blogApp=zmlcts HTTP/1.1";

        Pattern pattern = Pattern.compile("^([a-zA-Z]*) [htps]{0,4}[:/]{0,3}(\\S[^/]*)(\\S*) (\\S*)");

//        pattern.matcher(src);

        boolean isHostFound = true;
        Map<String, String> header = new HashMap<>();
//        if (1 > 0) {
            Matcher matcher = pattern.matcher(src);
//            if (matcher.find()) {
//                header.put("method", matcher.group(1));
//                if (matcher.group(2).startsWith("/")) {
//                    header.put("url", "/");
//                    isHostFound = false;
//                }
//                else {
//                    header.put("host", matcher.group(2));
//                    header.put("url", matcher.group(3));
//                }
//                header.put("version", matcher.group(4));
//            }
//        }

//        if (!isHostFound) {
//            for (String line : httpHeaders) {
//                if (line.toLowerCase().contains("host")) {
//                    String info = line.split(":")[1].trim();
//                    header.put("host", info);
//                    break;
//                }
//            }
//        }


    }

}
