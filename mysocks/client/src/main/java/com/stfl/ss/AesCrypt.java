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

package com.stfl.ss;

import com.stfl.util.console.Console;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * AES Crypt implementation
 */

/**
 * 在shadowsocks中，他的秘钥使用的时候实际上是进行了一次md5，
 * 这个算法应该是一种约定
 */
public class AesCrypt extends CryptBase {

    public final static String CIPHER_AES_128_CFB = "aes-128-cfb";
    public final static String CIPHER_AES_192_CFB = "aes-192-cfb";
    public final static String CIPHER_AES_256_CFB = "aes-256-cfb";
    public final static String CIPHER_AES_128_OFB = "aes-128-ofb";
    public final static String CIPHER_AES_192_OFB = "aes-192-ofb";
    public final static String CIPHER_AES_256_OFB = "aes-256-ofb";

    public static Map<String, String> getCiphers() {
        Map<String, String> ciphers = new HashMap<>();
        ciphers.put(CIPHER_AES_128_CFB, AesCrypt.class.getName());
        ciphers.put(CIPHER_AES_192_CFB, AesCrypt.class.getName());
        ciphers.put(CIPHER_AES_256_CFB, AesCrypt.class.getName());
        ciphers.put(CIPHER_AES_128_OFB, AesCrypt.class.getName());
        ciphers.put(CIPHER_AES_192_OFB, AesCrypt.class.getName());
        ciphers.put(CIPHER_AES_256_OFB, AesCrypt.class.getName());

        return ciphers;
    }

    public AesCrypt(String name, String password) {
        super(name, password);
    }

    @Override
    public int getKeyLength() {
        if(_name.equals(CIPHER_AES_128_CFB) || _name.equals(CIPHER_AES_128_OFB)) {
            return 16;
        }
        else if (_name.equals(CIPHER_AES_192_CFB) || _name.equals(CIPHER_AES_192_OFB)) {
            return 24;
        }
        else if (_name.equals(CIPHER_AES_256_CFB) || _name.equals(CIPHER_AES_256_OFB)) {
            return 32;
        }

        return 0;
    }

    @Override
    protected StreamBlockCipher getCipher(boolean isEncrypted) throws InvalidAlgorithmParameterException {
        AESFastEngine engine = new AESFastEngine();
        StreamBlockCipher cipher;

        if (_name.equals(CIPHER_AES_128_CFB)) {
            cipher = new CFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (_name.equals(CIPHER_AES_192_CFB)) {
            cipher = new CFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (_name.equals(CIPHER_AES_256_CFB)) {
            cipher = new CFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (_name.equals(CIPHER_AES_128_OFB)) {
            cipher = new OFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (_name.equals(CIPHER_AES_192_OFB)) {
            cipher = new OFBBlockCipher(engine, getIVLength() * 8);
        }
        else if (_name.equals(CIPHER_AES_256_OFB)) {
            cipher = new OFBBlockCipher(engine, getIVLength() * 8);
        }
        else {
            throw new InvalidAlgorithmParameterException(_name);
        }

        return cipher;
    }

    @Override
    public int getIVLength() {
        return 16;
    }

    @Override
    protected SecretKey getKey() {
        return new SecretKeySpec(_ssKey.getEncoded(), "AES");
    }

    @Override
    protected void _encrypt(byte[] data, ByteArrayOutputStream stream) {
        int noBytesProcessed;
        byte[] buffer = new byte[data.length];

        noBytesProcessed = encCipher.processBytes(data, 0, data.length, buffer, 0);
        stream.write(buffer, 0, noBytesProcessed);
    }

    @Override
    protected void _decrypt(byte[] data, ByteArrayOutputStream stream) {
        int noBytesProcessed;
        byte[] buffer = new byte[data.length];

        noBytesProcessed = decCipher.processBytes(data, 0, data.length, buffer, 0);
        stream.write(buffer, 0, noBytesProcessed);
    }


    public static void main(String[] args) throws IOException {
        CryptBase crypt = new AesCrypt("aes-256-cfb", "8888");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        crypt.encrypt("c".getBytes(), out);
        out.write("efg".getBytes());
        Console.printBytesAndString("加密后的数据是",out.toByteArray());
        ByteArrayOutputStream dec_data = new ByteArrayOutputStream();
        crypt.decrypt(out.toByteArray(), dec_data);
        Console.printBytesAndString("解密后的数据是",dec_data.toByteArray());

        /*crypt.set_encryptIVSet(false);
        crypt.encrypt("h".getBytes(), out);
        Console.printBytesAndString("二次加密后的数据",out.toByteArray());
*/
        /**
         * 测试发现，只要不改变iv，就会默认使用流加密的方式进行加密，也就是后面的都会使用前面的流进行加密,
         * 如果重置了加密iv就不行了
         */

        /**
         * 对加密的过程进行总结
         */

    }


}
