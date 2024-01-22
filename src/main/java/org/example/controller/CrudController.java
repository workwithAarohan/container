package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.abstraction.annotation.*;
import org.example.model.Contact;
import org.example.service.CrudService;
import java.util.List;

@RequiredArgsConstructor
public class CrudController<T, L> {
    private final CrudService<T, L> service;

    @GetMapping
    public List<T> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable("id") L id) {
        return service.getById(id);
    }

    @PostMapping
    public T save(@RequestBody T entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public T update(@RequestBody T entity, @PathVariable("id") L id) {
        return service.update(entity, id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") L id) {
        service.deleteById(id);
    }
}