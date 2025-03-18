package com.inventory.app.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public abstract class JpaBaseDAO<T> implements BaseDAO<T> {
    protected final EntityManager entityManager;
    protected final Class<T> entityClass;

    protected JpaBaseDAO(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        try {
            entityManager.getTransaction().begin();
            if (entityManager.contains(entity)) {
                entity = entityManager.merge(entity);
            } else {
                entityManager.persist(entity);
            }
            entityManager.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rootEntry = cq.from(entityClass);
        CriteriaQuery<T> all = cq.select(rootEntry);
        TypedQuery<T> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }

    @Override
    public void delete(T entity) {
        try {
            entityManager.getTransaction().begin();
            if (!entityManager.contains(entity)) {
                entity = entityManager.merge(entity);
            }
            entityManager.remove(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public long count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(entityClass)));
        return entityManager.createQuery(cq).getSingleResult();
    }

    protected void beginTransaction() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }

    protected void commitTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }

    protected void rollbackTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
    }
} 