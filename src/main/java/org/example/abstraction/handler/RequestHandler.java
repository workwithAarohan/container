package org.example.abstraction.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.abstraction.annotation.RequestBody;
import org.example.abstraction.bean.ApplicationContext;
import org.example.abstraction.annotation.PathVariable;
import org.example.abstraction.mapping.RequestMapper;
import org.example.abstraction.response.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class RequestHandler implements HttpHandler {
    private final List<RequestMapper> mapper;
    private final ApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        log.info("Initializing RequestHandler 'requestHandler'");
        String requestMethod = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        HttpResponse response = new HttpResponse();

        RequestMapper matchingMapper = findMatchingMapper(requestMethod, uri);

        if (matchingMapper != null) {
            Map<String, String> pathVariables = extractPathVariables(matchingMapper.getUri(), uri);
            Object obj = invokeControllerMethod(matchingMapper, pathVariables, exchange);
            response.sendJsonResponse(exchange, obj, 200);
            log.info("Successful");
        } else {
            response.sendJsonResponse(exchange, "404 Not Found", 404);
            log.error("No matching uri");
        }
    }

    private RequestMapper findMatchingMapper(String requestMethod, String uri) {
        for (RequestMapper requestMapper : mapper) {
            if (requestMapper.getRequestMethod().equalsIgnoreCase(requestMethod)
                    && matchUri(requestMapper.getUri(), uri)) {
                return requestMapper;
            }
        }
        return null;
    }

    private boolean matchUri(String pattern, String uri) {
        if(pattern.equals(uri)) {
            return true;
        }

        if (!pattern.contains("{") && !pattern.contains("}")) {
            return false;
        }

        String[] patternSegments = pattern.split("/");
        String[] uriSegments = uri.split("/");

        if (patternSegments.length != uriSegments.length) {
            return false;
        }

        for (int i = 0; i < patternSegments.length; i++) {
            if (!isSegmentMatch(patternSegments[i], uriSegments[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean isSegmentMatch(String patternSegment, String uriSegment) {
        if (patternSegment.equals(uriSegment)) {
            return true;
        }

        return patternSegment.startsWith("{") && patternSegment.endsWith("}");
    }

    private Map<String, String> extractPathVariables(String pattern, String uri) {
        Map<String, String> pathVariables = new HashMap<>();
        String[] patternSegments = pattern.split("/");
        String[] uriSegments = uri.split("/");

        for (int i = 0; i < patternSegments.length; i++) {
            if (patternSegments[i].startsWith("{") && patternSegments[i].endsWith("}")) {
                String variableName = patternSegments[i].substring(1, patternSegments[i].length() - 1);
                pathVariables.put(variableName, uriSegments[i]);
            }
        }

        return pathVariables;
    }

    private Object invokeControllerMethod(RequestMapper mapper, Map<String, String> pathVariables, HttpExchange exchange) {
        Object response = null;
        try {
            Class<?> clazz = Class.forName(mapper.getClassName());
            Class<?>[] parameterTypes = mapper.getParameters().stream()
                    .map(Parameter::getType).toList()
                    .toArray(new Class<?>[0]);

            Method method = findMethod(clazz, mapper.getMethodName(), parameterTypes);
            Parameter[] parameters = Objects.requireNonNull(method).getParameters();

            Object[] arguments = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];

                if (parameter.isAnnotationPresent(PathVariable.class)) {
                    PathVariable pathVariableAnnotation = parameter.getAnnotation(PathVariable.class);
                    String variableName = pathVariableAnnotation.value();
                    String variableValue = pathVariables.get(variableName);
                    Class<?> parameterType = getClass(clazz, parameter, 1);
                    arguments[i] = convertToType(variableValue, parameterType);
                } else if(parameter.isAnnotationPresent(RequestBody.class)) {
                    Class<?> parameterType = getClass(clazz, parameter, 0);
                    String requestBody = readRequestBody(exchange);
                    arguments[i] = convertRequestBodyToObject(requestBody, parameterType);
                }
            }

            Object instance = context.getBean(clazz);

            response = method.invoke(instance, arguments);

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage());
        }

        return response;
    }

    private static Class<?> getClass(Class<?> bean, Parameter parameter, int index) {
        if (bean.getGenericSuperclass() instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0 && typeArguments[index] instanceof Class) {
                return (Class<?>) typeArguments[index];
            }
        }
        return parameter.getType();
    }

    private String readRequestBody(HttpExchange exchange) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            return requestBody.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return findMethod(superclass, methodName, parameterTypes);
            }
            return null;
        }
    }

    private Object convertToType(String value, Class<?> targetType) {
        if (targetType.equals(Long.class)) {
            return Long.parseLong(value);
        }
        return null;
    }

    private Object convertRequestBodyToObject(String requestBody, Class<?> targetType) {
        try {
            return objectMapper.readValue(requestBody, targetType);
        } catch (IOException e) {
            log.error("Error converting request body to object", e);
            return null;
        }
    }
}