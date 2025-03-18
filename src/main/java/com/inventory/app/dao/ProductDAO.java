package com.inventory.app.dao;

import com.inventory.app.model.Product;
import com.inventory.app.model.Category;
import java.util.List;

public interface ProductDAO extends BaseDAO<Product> {
    List<Product> findByCategory(Category category);
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    List<Product> findLowStock(int threshold);
    boolean updateStock(Long productId, int quantity);
} 