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

package com.stfl.network.io;

import com.stfl.Constant;
import com.stfl.misc.Config;
import com.stfl.misc.Util;
import com.stfl.network.proxy.IProxy;
import com.stfl.network.proxy.ProxyFactory;
import com.stfl.ss.CryptFactory;
import com.stfl.ss.ICrypt;
import com.stfl.util.console.Console;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * Pipe local and remote sockets while server is running under blocking mode.
 */
public class PipeSocket implements Runnable {
    private Logger logger = Logger.getLogger(PipeSocket.class.getName());

    private final int TIMEOUT = 10000; // 10s
    private ByteArrayOutputStream _remoteOutStream;
    private ByteArrayOutputStream _localOutStream;
    private Socket _remote;
    private Socket _local;
    private IProxy _proxy;
    private ICrypt pass_crypt;
    private ICrypt _crypt;
    private boolean _isClosed;
    private Executor _executor;
    private Config _config;
    private boolean _authored_remote = false;

    public PipeSocket(Executor executor, Socket socket, Config config) throws IOException {
        _executor = executor;
        _local = socket;
        _local.setSoTimeout(TIMEOUT);
        _config = config;
        _crypt = CryptFactory.get(_config.getMethod(), _config.getUserPassWord());
        pass_crypt = CryptFactory.get(_config.getMethod(), _config.getPassword());
        _proxy = ProxyFactory.get(_config.getProxyType());
        _remoteOutStream = new ByteArrayOutputStream(Constant.BUFFER_SIZE);
        _localOutStream = new ByteArrayOutputStream(Constant.BUFFER_SIZE);
    }

    @Override
    public void run() {
        try {
            _remote = initRemote(_config);
            _remote.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            close();
            logger.warning(Util.getErrorMessage(e));
            return;
        }

        _executor.execute(getLocalWorker());
        _executor.execute(getRemoteWorker());
    }

    private Socket initRemote(Config config) throws IOException {
        return new Socket(config.getRemoteIpAddress(), config.getRemotePort());
    }

    /**
     * 获取本地代理和浏览器以及远端代理的交互任务
     *
     * @return
     */
    private Runnable getLocalWorker() {
        return new Runnable() {
            @Override
            public void run() {
                BufferedInputStream stream;
                byte[] dataBuffer = new byte[Constant.BUFFER_SIZE];
                byte[] buffer;
                int readCount;
                List<byte[]> sendData = null;

                // prepare local stream
                try {
                    stream = new BufferedInputStream(_local.getInputStream());

                } catch (IOException e) {
                    logger.info(e.toString());
                    return;
                }

                // start to process data from local socket
                while (true) {
                    try {
                        // read data
                        readCount = stream.read(dataBuffer);
                        if (readCount == -1) {
                            throw new IOException("Local socket closed (Read)!");
                        }
                        byte[] temp;
                        buffer = new byte[readCount];

                        // dup dataBuffer to use in later
                        System.arraycopy(dataBuffer, 0, buffer, 0, readCount);

                        Console.printString("浏览器发过来的数据是：", buffer);

                        // initialize proxy,如果没有准备好，说明依然需要进行一些协议层面的协商，不能进行数据的直接转发
                        if (!_proxy.isReady()) {
//                            Console.simplePrint("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!进入没有准备的模式");


//                            Console.printBytesAndString("没有进入准备状态下从浏览器发送过来的请求参数是", dataBuffer);


                            temp = _proxy.getResponse(buffer);
//                            Console.printBytesAndString("没有进入准备状态下发送给浏览器的数据", temp);


                            if ((temp != null) && (!_sendLocal(temp, temp.length))) {
                                throw new IOException("Local socket closed (proxy-Write)!");
                            }
                            // packet for remote socket
                            sendData = _proxy.getRemoteResponse(buffer);

                            if (sendData == null) {
                                continue;
                            } else {
                                if (!_authored_remote) {
                                    sendUserNameRemote(sendData);
                                    _authored_remote = true;
                                    continue;
                                }
                            }

                            logger.info("Connected to: " + Util.getRequestedHostInfo(sendData.get(0)));

                        } else {

//                            Console.simplePrint("!!!!!!!!!!!!!!!!!!!!!!!!!!!!已经进入了准备状态");

                            //这个时候服务器已经准备好，只是需要直接转发就可以了
                            sendData.clear();
                            sendData.add(buffer);

                            for (byte[] a_group_data : sendData) {

//                                Console.printBytesAndString("已经进入准备状态下发送远端服务器的数据222",a_group_data);
//                                Console.printBytesAndString("已经进入准备状态下发送远端服务器的数据222", a_group_data);
                            }

                        }


                        //发送数据到远端
                        for (byte[] bytes : sendData) {
                            // send data to remote socket
                            if (!sendRemote(bytes, bytes.length)) {
                                throw new IOException("Remote socket closed (Write)!");
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        continue;
                    } catch (IOException e) {
                        logger.fine(e.toString());
                        break;
                    }
                }
                close();
                logger.fine(String.format("localWorker exit, Local=%s, Remote=%s", _local, _remote));
            }
        };
    }

    private void sendUserNameRemote(List<byte[]> sendData) throws IOException {

        String user_name = _config.getUserPassWord();
        int name_len = Util.getUtf8_length(user_name);
        byte[] user_pass = new byte[name_len + 1];
        user_pass[0] = (byte) name_len;
        System.arraycopy(user_name.getBytes(), 0, user_pass, 1, name_len);
        ByteArrayOutputStream pass_out = new ByteArrayOutputStream();
        ByteArrayOutputStream content_out = new ByteArrayOutputStream();

        for (byte[] a_first_data : sendData) {
            content_out.write(a_first_data);
        }
        byte[] content = content_out.toByteArray();
        content_out.reset();
        pass_crypt.encrypt(user_pass, pass_out);
        _crypt.encrypt(content, content_out);
        pass_out.write(content_out.toByteArray());

        _sendRemote(pass_out.toByteArray(), pass_out.toByteArray().length);

    }

    /**
     * 获取远端代理的的数据解密，格式转换以后回传本地
     *
     * @return
     */
    private Runnable getRemoteWorker() {
        return new Runnable() {
            @Override
            public void run() {
                BufferedInputStream stream;
                int readCount;
                byte[] dataBuffer = new byte[4096];

                // prepare remote stream
                try {
                    //stream = _remote.getInputStream();
                    stream = new BufferedInputStream(_remote.getInputStream());
                } catch (IOException e) {
                    logger.info(e.toString());
                    return;
                }

                // start to process data from remote socket
                while (true) {
                    try {
                        readCount = stream.read(dataBuffer);
                        if (readCount == -1) {
                            throw new IOException("Remote socket closed (Read)!");
                        }

//                        Console.printBytesAndString("" +
//                                "+++++++++++++++++++++++++++++从远端服务器接收到的数据是：",dataBuffer);

                        // send data to local socket
                        if (!sendLocal(dataBuffer, readCount)) {
                            throw new IOException("Local socket closed (Write)!");
                        }
                    } catch (SocketTimeoutException e) {
                        continue;
                    } catch (IOException e) {
                        logger.fine(e.toString());
                        break;
                    }

                }
                close();
                logger.fine(String.format("remoteWorker exit, Local=%s, Remote=%s", _local, _remote));
            }
        };
    }


    public void close() {
        if (_isClosed) {
            return;
        }
        _isClosed = true;

        try {
            _local.shutdownInput();
            _local.shutdownOutput();
            _local.close();
        } catch (IOException e) {
            logger.fine("PipeSocket_copy failed to close local socket (I/O exception)!");
        }
        try {
            if (_remote != null) {
                _remote.shutdownInput();
                _remote.shutdownOutput();
                _remote.close();
            }
        } catch (IOException e) {
            logger.fine("PipeSocket_copy failed to close remote socket (I/O exception)!");
        }
    }

    private boolean sendRemote(byte[] data, int length) {

        byte[] cosole_data = new byte[length];
        System.arraycopy(data, 0, cosole_data, 0, length);
        Console.printString("发送给远端的加密前数据：", cosole_data);

        _crypt.encrypt(data, length, _remoteOutStream);
        byte[] sendData = _remoteOutStream.toByteArray();

        return _sendRemote(sendData, sendData.length);
    }

    private boolean _sendRemote(byte[] data, int length) {
        try {
            if (length > 0) {
                OutputStream outStream = _remote.getOutputStream();
                outStream.write(data, 0, length);
            } else {
                logger.info("Nothing to sendRemote!\n");
            }
        } catch (IOException e) {
            logger.info(Util.getErrorMessage(e));
            return false;
        }

        return true;
    }

    private boolean sendLocal(byte[] data, int length) {
        _crypt.decrypt(data, length, _localOutStream);
        byte[] sendData = _localOutStream.toByteArray();

        return _sendLocal(sendData, sendData.length);
    }

    private boolean _sendLocal(byte[] data, int length) {
        try {

            byte[] console_date = new byte[length];
            System.arraycopy(data, 0, console_date, 0, length);
            Console.printString("发送给浏览器的数据是：", console_date);

            OutputStream outStream = _local.getOutputStream();
            outStream.write(data, 0, length);
        } catch (IOException e) {
            logger.info(Util.getErrorMessage(e));
            return false;
        }
        return true;
    }
}
