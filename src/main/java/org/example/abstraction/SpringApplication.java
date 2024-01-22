package org.example.abstraction;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.example.Application;
import org.example.abstraction.annotation.*;
import org.example.abstraction.bean.ApplicationContext;
import org.example.abstraction.bean.BeanFactory;
import org.example.abstraction.exception.DependencyNotFoundException;
import org.example.abstraction.handler.RequestHandler;
import org.example.abstraction.mapping.RequestMapper;
import org.example.abstraction.util.CommonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class SpringApplication {
    private static final List<RequestMapper> mapper = new ArrayList<>();
    private static final BeanFactory beanFactory = new BeanFactory();
    private static final ApplicationContext context = new ApplicationContext();

    public static void run(Class<?> clazz) {
        printBanner();
        try {
            manageBeans(clazz);
            handleRequest(clazz);
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
        }
    }

    private static void printBanner() {
        InputStream inputStream = Application.class.getResourceAsStream("/banner.txt");
        if (inputStream != null) {
            try (Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNextLine()) {
                    System.out.println(scanner.nextLine());
                }
            }
        }
    }

    private static void handleRequest(Class<?> clazz) throws IOException {
        log.info("Starting service");
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        registerHandlers(clazz);
        server.createContext("/", new RequestHandler(mapper, context));

        server.setExecutor(null);
        server.start();

        log.info("Server started on port 8080");
    }

    private static void manageBeans(Class<?> clazz) {
        log.info("Initializing Application Context");
        beanFactory.resolveDependencies(clazz);
        try {
            context.instantiateAndStoreObjects(beanFactory.getResolvedDependencies());
            log.info("Root ApplicationContext: initialization completed");
        } catch (DependencyNotFoundException e) {
            log.error("DependencyNotFoundException: {}", e.getMessage());
        }
    }

    private static void registerHandlers(Class<?> clazz) {
        String basePackage = clazz.getPackageName();
        List<Class<?>> allRestControllerClasses = CommonUtil.getClassesInPackage(basePackage, RestController.class);

        for (Class<?> bean : allRestControllerClasses) {
            processClass(bean);
        }
    }

    private static void processClass(Class<?> bean) {
        if (bean.isAnnotationPresent(RequestMapping.class)) {
            String requestUri = bean.getAnnotation(RequestMapping.class).value();
            Class<?> originalBean = bean;
            while(bean != null && !bean.isInstance(Object.class)) {
                for (Method method : bean.getDeclaredMethods()) {
                    processControllerMethod(originalBean, method, Arrays.asList(method.getParameters()), requestUri, GetMapping.class,
                            PostMapping.class, DeleteMapping.class, PutMapping.class);
                }
                bean = bean.getSuperclass();
            }
        }
    }

    @SafeVarargs
    private static void processControllerMethod(Class<?> bean, Method method, List<Parameter> parameters,
                                                String requestUri, Class<? extends Annotation>... annotatedClass) {
        for (Class<? extends Annotation> annotation :annotatedClass){
            if (method.isAnnotationPresent(annotation)) {
                mapper.add(RequestMapper.builder()
                        .className(bean.getName())
                        .methodName(method.getName())
                        .uri(requestUri + getAnnotationValue(method.getAnnotation(annotation), "value"))
                        .requestMethod(getAnnotationValue(method.getAnnotation(annotation), "method"))
                        .parameters(parameters)
                        .build()
                );
            }
        }
    }

    private static String getAnnotationValue(Annotation annotation, String attribute) {
        try {
            Method methodMethod = annotation.annotationType().getDeclaredMethod(attribute);
            return (String) methodMethod.invoke(annotation);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
        }
        return "";
    }
}