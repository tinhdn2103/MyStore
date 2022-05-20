package com.example.mystore.models;

import java.io.Serializable;
import java.util.Date;

public class CartProduct implements Serializable {
    private String id;
    private Product product;
    private User user;
    private Date dateTime;
    private long quantity;

    public CartProduct() {
    }

    public CartProduct(Product product, User user, Date dateTime, long quantity) {
        this.product = product;
        this.user = user;
        this.dateTime = dateTime;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
