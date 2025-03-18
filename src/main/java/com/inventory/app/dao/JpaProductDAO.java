package com.inventory.app.dao;

import com.inventory.app.model.Product;
import com.inventory.app.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

public class JpaProductDAO extends JpaBaseDAO<Product> implements ProductDAO {
    
    public JpaProductDAO(EntityManager entityManager) {
        super(entityManager, Product.class);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.where(cb.equal(root.get("category"), category));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.where(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Product> findByPriceBetween(double minPrice, double maxPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.where(cb.between(root.get("price"), minPrice, maxPrice));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Product> findLowStock(int threshold) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.where(cb.le(root.get("quantity"), threshold));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public boolean updateStock(Long productId, int quantity) {
        try {
            beginTransaction();
            Product product = findById(productId).orElse(null);
            if (product != null) {
                product.setQuantity(quantity);
                save(product);
                commitTransaction();
                return true;
            }
            return false;
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    // Additional helper methods
    public List<Product> findOutOfStock() {
        return findLowStock(0);
    }

    public List<Product> findTopSelling(int limit) {
        String jpql = "SELECT p, SUM(oi.quantity) as total FROM Product p " +
                     "JOIN OrderItem oi ON oi.product = p " +
                     "GROUP BY p ORDER BY total DESC";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
} 