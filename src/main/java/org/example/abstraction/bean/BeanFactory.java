package org.example.abstraction.bean;

import lombok.extern.slf4j.Slf4j;
import org.example.abstraction.annotation.Component;
import org.example.abstraction.annotation.Repository;
import org.example.abstraction.annotation.RestController;
import org.example.abstraction.annotation.Service;
import org.example.abstraction.util.CommonUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class BeanFactory {
    private final Map<Class<?>, Bean> classNodeMap = new HashMap<>();
    private final List<Bean> resolvedDependencies = new ArrayList<>();

    public void resolveDependencies(Class<?> clazz) {
        String basePackage = clazz.getPackageName();
        List<Class<?>> allComponentClasses = CommonUtil.getClassesInPackage(basePackage, Component.class,
                RestController.class, Service.class, Repository.class);

        buildDependencyGraph(allComponentClasses);
        resolveDependenciesInternal();
    }

    private void buildDependencyGraph(List<Class<?>> allComponentClasses) {
        for (Class<?> clazz : allComponentClasses) {
            Bean node = getNode(clazz);
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                for (Parameter parameter : constructor.getParameters()) {
                    Class<?> paramType = parameter.getType();
                    if (paramType.isInterface()) {
                        Class<?> implementationClass = findImplementationClass(paramType);
                        if (implementationClass != null) {
                            Bean dependencyNode = getNode(implementationClass);
                            node.addDependency(dependencyNode);
                        }
                    } else {
                        Bean dependencyNode = getNode(paramType);
                        node.addDependency(dependencyNode);
                    }
                }
            }
        }
    }

    private void resolveDependenciesInternal() {
        Set<Bean> visited = new HashSet<>();
        for (Bean node : classNodeMap.values()) {
            resolveDependencies(node, visited);
        }
    }

    private void resolveDependencies(Bean node, Set<Bean> visited) {
        if (!visited.contains(node)) {
            visited.add(node);

            for (Bean dependency : node.getDependencies()) {
                resolveDependencies(dependency, visited);
            }

            resolvedDependencies.add(node);
        }
    }

    private Bean getNode(Class<?> clazz) {
        return classNodeMap.computeIfAbsent(clazz, Bean::new);
    }

    public List<Bean> getResolvedDependencies() {
        return resolvedDependencies;
    }

    public static Class<?> findImplementationClass(Class<?> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("Provided class is not an interface.");
        }

        List<Class<?>> implementations = new ArrayList<>();

        for (Class<?> clazz : CommonUtil.getClassesInPackage(interfaceClass.getPackage().getName(), Component.class,
                RestController.class, Service.class, Repository.class)) {
            if (interfaceClass.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                implementations.add(clazz);
            }
        }

        if (!implementations.isEmpty()) {
            return implementations.get(0);
        }

        return null;
    }
}

