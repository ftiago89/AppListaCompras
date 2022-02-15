package com.felipe.nossafeira.models;

public class ShoppingList {

    private final int id;
    private final String name;
    private final String createdDate;

    public ShoppingList(int id, String name, String createdDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedDate() {
        return createdDate;
    }
}
