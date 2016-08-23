package com.weinong.lab.config;

import com.weinong.lab.constst.Consts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/18.
 */
public class FileUtil {

    /**
     * 读取配置文件
     * @param path
     * @return
     */
    public static String getFileContent(String path) {

        File file = new File(path);
        return getFileContent(file);
    }

    /**
     * 读取配置文件
     * @param file
     * @return
     */
    public static String getFileContent(File file) {

        InputStream in =null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            in = new FileInputStream(file);
            byte[] buffer_temp = new byte[Consts.buffer_size];
            int read_len = 0;

            while ((read_len = in.read(buffer_temp)) !=-1) {
                out.write(buffer_temp, 0, read_len);

            }
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("read the com.weinong.lab.config file meet an exception !", e);
        }

    }


    public static void main(String[] args) {
        URL url = FileUtil.class.getClassLoader().getResource("shadow_config.json");
        String content = getFileContent(url.getPath());
        System.out.println(content);

        content = content.replace("\n", "");
        content = content.replace("\r", "");
        System.out.println(content);

    }
}
