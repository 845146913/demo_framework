package com.example.util;

import com.example.core.annotations.Controller;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 类加载工具
 */
public class ClassUtils {

    private static final Logger log = Logger.getLogger(ClassUtils.class.getName());

    /**
     * class文件的后缀
     */
    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * class分隔符：点
     */
    private static final String CLASS_FILE_POINT = ".";
    /**
     * 包文件分隔符：点
     */
    private static final String PACKAGE_FILE_POINT = CLASS_FILE_POINT;
    /**
     * 文件路径分割符：/
     */
    private static final String PATH_SEPARATOR = "/";


    /**
     * 当前线程的类加载
     */
    private static final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

    public static ClassLoader getCurrentClassLoader() {
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
            log.info("加载类错误.类未找到." + e);
            throw new RuntimeException(e);
        }
        return clazz;
    }

    /**
     * 获取包下的所有类
     *
     * @param packageName
     * @return
     */
    public static List<Class<?>> getClassList(String packageName) {
        List<Class<?>> classList = new ArrayList<>();
        try {
//            URL url = getCurrentClassLoader().getResource(PATH_SEPARATOR + packageName.replace(CLASS_FILE_POINT, PATH_SEPARATOR));
//            addClass(classList, url.getFile());
            // 从包名获取 URL 类型的资源
            Enumeration<URL> urlList = getCurrentClassLoader()
                    .getResources(packageName.replace(CLASS_FILE_POINT, PATH_SEPARATOR));
            while (urlList.hasMoreElements()) {
                URL url = urlList.nextElement();
                if (url != null) {
                    // 获取 URL 协议名 [file] [jar]
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        // 若在 class 目录,添加类
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classList, packagePath, packageName);
                    } else if ("jar".equals(protocol)) {
                        // TODO 若在 jar 包中,则解析 jar 包中的 entry
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        JarFile jarFile = jarURLConnection.getJarFile();
                        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                        while (jarEntryEnumeration.hasMoreElements()) {
                            JarEntry jarEntry = jarEntryEnumeration.nextElement();
                            String jarEntryName = jarEntry.getName();
                            log.info("jar entryName: " + jarEntryName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("类扫描失败." + e);
        }
        return classList;
    }

    /**
     * 获取指定包下包含相应注解及附加条件的所有类
     *
     * @param packageName
     * @param annotation
     * @param action
     * @param <E>
     * @return
     */
    public static <E extends Annotation> List<Class<?>> getClassListByAnnotation(String packageName, Class<E> annotation, ConditionCallback action) {
        return getClassList(packageName).stream()
                .filter(clz -> clz.isAnnotationPresent(annotation) &&
                        (action != null ? action.doInCondition() : true))
                .collect(Collectors.toList());
    }

    /**
     * 添加类【递归】
     *
     * @param classList
     * @param packagePath
     */
    private static void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            File dir = new File(packagePath);
            // 所有的class类和文件夹
            File[] files = dir.listFiles(file -> file.isFile() && file.getName().endsWith(CLASS_FILE_SUFFIX) || file.isDirectory());
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile()) {
                    // 是类名
                    String className = fileName.substring(0, fileName.lastIndexOf(CLASS_FILE_POINT));
                    if (null != className && !"".equals(className)) {
                        className = packageName + PACKAGE_FILE_POINT + className;
                    }
                    Class<?> aClass = loadClass(className, false);
                    classList.add(aClass);
                } else {
                    String subpackagePath = packagePath + (packagePath.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR) + fileName;
                    String subpackageName = packageName + (null != packageName && !packageName.equals("") ? PACKAGE_FILE_POINT : "") + fileName;
                    addClass(classList, subpackagePath, subpackageName);
                }
            }
        } catch (Exception e) {
            log.info("类添加失败");
        }
    }

    public static void main(String[] args) {
        List<Class<?>> classList = ClassUtils.getClassListByAnnotation("", Controller.class, null);
        System.out.println(classList);
    }

    interface ConditionCallback {
        boolean doInCondition() throws RuntimeException;
    }
}
