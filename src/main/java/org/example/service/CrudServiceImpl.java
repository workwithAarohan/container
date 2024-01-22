package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CrudServiceImpl<T, L> implements CrudService<T, L> {
    private final CrudRepository<T, L> repository;

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public T getById(L id) {
        return repository.findById(id);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public T update(T entity, L id) {
        return repository.update(entity, id);
    }


    @Override
    public void deleteById(L id) {
        repository.deleteById(id);
    }
}
