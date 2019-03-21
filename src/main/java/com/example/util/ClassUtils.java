package com.example.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
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

    private static ClassLoader getCurrentClassLoader() {
        return currentClassLoader;
    }

    /**
     * 加载类
     *
     * @param className  类名 com.example.XX
     * @param initialize true or false
     * @return cls
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
     * @param packageName 包名
     * @return clz
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
                            if (jarEntryName.endsWith(".class")){
                                String className= jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                                doAddClass(classList,className);
                            }
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
     * 获取指定包下包含相应注解的所有类
     *
     * @param packageName baoming
     * @param annotation  注解类clz
     * @param <E>         cls
     * @return clz
     */
    public static <E extends Annotation> List<Class<?>> getClassListByAnnotation(String packageName, Class<E> annotation) {
        return getClassList(packageName).stream()
                .filter(clz -> clz.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定包下符合自定义条件的所有类
     *
     * @param packageName 包名
     * @param action      条件回调类
     * @return clz
     */
    @SuppressWarnings("unchecked")
    public static <T> List<Class<?>> getClassListByCondition(String packageName, ConditionCallback<T> action) {
        return getClassList(packageName).stream()
                .filter(clz -> action.doInCondition((T) clz))
                .collect(Collectors.toList());
    }

    /**
     * 添加类【递归】
     *
     * @param classList   类
     * @param packagePath 包路径
     */
    private static void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            File dir = new File(packagePath);
            // 所有的class类和文件夹
            Optional<File[]> files = Optional.ofNullable(dir.listFiles(file -> file.isFile() && file.getName().endsWith(CLASS_FILE_SUFFIX) || file.isDirectory()));
            if (files.isPresent())
                for (File file : files.get()) {
                    String fileName = file.getName();
                    if (file.isFile()) {
                        // 是类名
                        String className = fileName.substring(0, fileName.lastIndexOf(CLASS_FILE_POINT));
                        if (!"".equals(className)) {
                            className = packageName + PACKAGE_FILE_POINT + className;
                        }
                        doAddClass(classList, className);
                    } else {
                        String subpackagePath = packagePath + (packagePath.endsWith(PATH_SEPARATOR) ? "" : PATH_SEPARATOR) + fileName;
                        String subpackageName = packageName + (!packageName.equals("") ? PACKAGE_FILE_POINT : "") + fileName;
                        addClass(classList, subpackagePath, subpackageName);
                    }
                }
        } catch (Exception e) {
            log.info("类添加失败");
        }
    }

    private static void doAddClass(Collection<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }


    public interface ConditionCallback<T> {
        boolean doInCondition(T o) throws RuntimeException;
    }
}
