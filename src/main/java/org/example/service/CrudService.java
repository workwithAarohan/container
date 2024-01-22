package org.example.service;

import java.util.List;
import java.util.Optional;

public interface CrudService<T, L> {
    List<T> getAll();
    T getById(L id);

    T save(T entity);

    T update(T entity, L id);

    void deleteById(L id);
}