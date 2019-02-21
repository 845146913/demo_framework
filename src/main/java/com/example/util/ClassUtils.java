package com.example.util;

import java.lang.Thread;
import java.util.logging.Logger;

/**
 * 类加载工具
 */
public class ClassUtils {

    private static final Logger log = Logger.getLogger(ClassUtils.class.getName());

    /**
     * 当前线程的类加载
     */
    private static final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

    public static ClassLoader getCurrentClassLoader(){
        return currentClassLoader;
    }

    /**
     * 加载类
     *
     * @param className
     * @param initialize
     * @return
     */
    public static Class<?> loadClass(String className, Boolean initialize) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className, initialize, getCurrentClassLoader());
        } catch (ClassNotFoundException e) {
            log.info("加载类错误");
            throw new RuntimeException(e);
        }
        return clazz;
    }
}
