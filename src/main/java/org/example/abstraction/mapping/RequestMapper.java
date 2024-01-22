package org.example.abstraction.mapping;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Parameter;
import java.util.List;

@Data
@Builder
public class RequestMapper {
    private String className;
    private String uri;
    private String requestMethod;
    private String methodName;
    private List<Parameter> parameters;
}