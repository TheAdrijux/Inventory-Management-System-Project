package com.inventory.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Constructors
    public Product() {}

    public Product(String name, BigDecimal price, Integer quantity, Category category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Business methods
    public boolean hasEnoughStock(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }

    public void reduceStock(int amount) {
        if (hasEnoughStock(amount)) {
            quantity -= amount;
        } else {
            throw new IllegalStateException("Not enough stock available");
        }
    }

    public void addStock(int amount) {
        quantity += amount;
    }

    @Override
    public String toString() {
        return name;
    }
} 