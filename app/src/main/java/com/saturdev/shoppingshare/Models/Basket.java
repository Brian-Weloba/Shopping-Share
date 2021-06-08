package com.saturdev.shoppingshare.Models;

public class Basket {
    private String name, price, quantity, description, items;

    public Basket() {
    }

    public Basket(String name, String price, String quantity, String description, String items) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}
