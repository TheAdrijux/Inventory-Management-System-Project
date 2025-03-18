package com.inventory.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // Default constructor
    public OrderItem() {}

    // Constructor with fields
    public OrderItem(Order order, Product product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.pricePerUnit = product.getPrice();
        this.unitPrice = product.getPrice();
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null && quantity != null) {
            this.pricePerUnit = product.getPrice();
            this.unitPrice = product.getPrice();
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (quantity != null && unitPrice != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        if (pricePerUnit != null && quantity != null) {
            this.totalPrice = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    protected void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Business methods
    public BigDecimal getSubtotal() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return product.getName() + " x " + quantity + " @ " + unitPrice;
    }
} 