package org.example.abstraction.bean;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Data
public class Bean {
    private final Class<?> clazz;
    private final List<Bean> dependencies = new ArrayList<>();

    public void addDependency(Bean dependency) {
        dependencies.add(dependency);
    }
}