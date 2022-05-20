package com.example.mystore.models;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private String id;
    private long price;
    private long quantity;
    private Product product;

    public OrderDetail() {
    }

    public OrderDetail(int price, int quantity, Product product) {
        this.price = price;
        this.quantity = quantity;
        this.product = product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
