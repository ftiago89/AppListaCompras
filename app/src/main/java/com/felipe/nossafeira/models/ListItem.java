package com.felipe.nossafeira.models;

public class ListItem {

    private final int id;
    private final int listId;
    private String name;
    private double price;
    private int quantity;
    private final String createdDate;
    private int isChecked;

    public ListItem(int id, int listId, String name, double price, int quantity, String createdDate, int isChecked) {
        this.id = id;
        this.listId = listId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.createdDate = createdDate;
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public int getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }
}
