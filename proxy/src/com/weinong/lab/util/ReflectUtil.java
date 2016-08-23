package com.weinong.lab.util;

import java.lang.reflect.Constructor;

/**
 * Created by Administrator on 2016/8/18.
 */
public class ReflectUtil {

    /**
     * 通过反射实例化类
     * @param class_name 要实例化的对象的全名
     * @param args 要调用的构造方法中的参数（不能为null）
     * @return
     */
    public static Object getInstance(String class_name, Object... args) {

        try {
            Class target_class = Class.forName(class_name);
            if (args.length == 0) {
                return target_class.newInstance();
            } else {
                Class[] param_type_array = getParamClass(args);

                Constructor constructor = target_class.getConstructor(param_type_array);
                return constructor.newInstance(args);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("init the class : " + class_name + "meet an error");
        }
    }

    /**
     * 获取菜蔬的类类型
     * @param args
     * @return
     */
    private static Class[] getParamClass(Object[] args) {

        Class[] class_array = new Class[args.length];
        for (int index = 0; index < args.length; index++) {
            class_array[index] = args.getClass();
        }

        return class_array;
    }
}
