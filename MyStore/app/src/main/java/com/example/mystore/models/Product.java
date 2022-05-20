package com.example.mystore.models;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    private String name, description, rate, img_url, id;
    private Category category;
    private Date dateCreated;
    private long price;
    private long quantity;

    public Product() {
    }

    public Product(String name, String description, String rate, String img_url, Category category, Date dateCreated, int price, int quantity) {
        this.name = name;
        this.description = description;
        this.rate = rate;
        this.img_url = img_url;
        this.category = category;
        this.dateCreated = dateCreated;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
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
}
