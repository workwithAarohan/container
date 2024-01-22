package org.example.abstraction.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.abstraction.exception.DependencyNotFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class ApplicationContext {
    private Map<Class<?>, Object> objectMap = new HashMap<>();

    public void addObject(Class<?> clazz, Object instance) {
        objectMap.put(clazz, instance);
    }

    public <T> T getObject(Class<T> clazz) {
        return clazz.cast(objectMap.get(clazz));
    }

    public <T> T getBean(Class<T> clazz) {
        return getObject(clazz);
    }

    public void instantiateAndStoreObjects(List<Bean> resolvedDependencies) {
        for (Bean bean : resolvedDependencies) {
            Class<?> clazz = bean.getClazz();
            List<Object> dependencies = instantiateDependencies(bean.getDependencies());

            try {
                Constructor<?> constructor = findSuitableConstructor(clazz.getDeclaredConstructors(), dependencies);

                if (constructor != null) {
                    Object instance = constructor.newInstance(dependencies.toArray());
                    addObject(clazz, instance);
                } else {
                    log.error("No suitable constructor found for class: {}", clazz.getName());
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("Error instantiating class: " + clazz.getName(), e);
            }
        }
    }

    private List<Object> instantiateDependencies(List<Bean> dependencyNodes) throws DependencyNotFoundException {
        List<Object> dependencies = new ArrayList<>();
        for (Bean dependencyNode : dependencyNodes) {
            Class<?> dependencyClass = dependencyNode.getClazz();
            Object dependencyInstance = getObject(dependencyClass);
            if (dependencyInstance == null) {
                log.error("Dependency instance not found for class: {}", dependencyClass.getName());
                throw new DependencyNotFoundException("Dependency instance not found for class: " + dependencyClass.getName());
            }
            dependencies.add(dependencyInstance);
        }
        return dependencies;
    }

    private Constructor<?> findSuitableConstructor(Constructor<?>[] constructors, List<Object> dependencies) {
        for (Constructor<?> constructor : constructors) {
            if (isSuitableConstructor(constructor, dependencies)) {
                return constructor;
            }
        }
        return null;
    }

    private boolean isSuitableConstructor(Constructor<?> constructor, List<Object> dependencies) {
        Parameter[] parameters = constructor.getParameters();
        if (parameters.length != dependencies.size()) {
            return false;
        }

        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameterType = parameters[i].getType();
            Class<?> dependencyType = dependencies.get(i).getClass();

            if (!parameterType.isAssignableFrom(dependencyType)) {
                return false;
            }
        }
        return true;
    }
}
