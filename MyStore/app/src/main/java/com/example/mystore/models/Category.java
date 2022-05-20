package com.example.mystore.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String img_url;
    private String name;
    private String description;
    private String id;

    public Category() {
    }

    public Category(String img_url, String name, String description) {
        this.img_url = img_url;
        this.name = name;
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
