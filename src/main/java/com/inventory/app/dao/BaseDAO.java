package com.inventory.app.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {
    T save(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(Long id);
    long count();
} 