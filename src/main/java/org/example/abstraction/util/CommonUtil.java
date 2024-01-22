package org.example.abstraction.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class CommonUtil {
    @SafeVarargs
    public static List<Class<?>> getClassesInPackage(String basePackage, Class<? extends Annotation>... annotatedClass) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            String path = basePackage.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                scanClassesInDirectory(directory, basePackage, classes, annotatedClass);
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error occurred while scanning classes in package", e);
        }

        return classes;
    }

    @SafeVarargs
    private static void scanClassesInDirectory(File directory, String basePackage, List<Class<?>> classes, Class<? extends Annotation>... annotationClasses) throws ClassNotFoundException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        scanClassesInDirectory(file, basePackage + "." + file.getName(), classes, annotationClasses);
                    } else if (file.getName().endsWith(".class")) {
                        String className = basePackage + '.' + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);

                        for (Class<? extends Annotation> annotationClass : annotationClasses) {
                            if (clazz.isAnnotationPresent(annotationClass)) {
                                classes.add(clazz);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
