package com.weinong.lab.util;

import java.util.Locale;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016/8/18.
 */
public class Log {

    /**
     * 对日志系统进行初始化
     *
     */

    private static boolean handler_init = false;

    public static void init() {
        init(Level.INFO);
    }

    public static void init(Level log_level) {

        Logger root_logger = getRootLogger();
        if (handler_init) {
            root_logger.setLevel(log_level);
            for (Handler handler : root_logger.getHandlers()) {
                handler.setLevel(log_level);
            }
            return;
        }

        //设置本地化
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);

        Properties props  = System.getProperties();//获取系统变量
        props.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tb-%1$td %1$tT [%4$s] %5$s%n");

        //将当前log设置为根log,移除所有的handlers=
        root_logger.setUseParentHandlers(false);
        for (Handler temp_handler : root_logger.getHandlers()) {
            root_logger.removeHandler(temp_handler);
        }

        //设置handler
        root_logger.setLevel(log_level);
        ConsoleHandler my_handler  = new ConsoleHandler();
        my_handler.setLevel(log_level);
        root_logger.addHandler(my_handler);

        handler_init = true;


    }

    /**
     * 获取根logger
     * @return
     */
    private static Logger getRootLogger() {

        return Logger.getLogger("com.weinong.lab");

    }

}
