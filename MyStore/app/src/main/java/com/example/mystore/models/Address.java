package com.example.mystore.models;

import java.io.Serializable;

public class Address implements Serializable {
    private String name, address, phone, id;
    private User user;
    private boolean isSelected;

    public Address() {
    }

    public Address(String name, String address, String phone, User user, boolean isSelected) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.user = user;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
