package org.example.repository;

import org.example.model.Document;

import java.util.List;

public interface CrudRepository<T, L> {
    List<T> findAll();
    T findById(L id);

    T save(T entity);

    T update(T entity, L id);

    void deleteById(L id);
}