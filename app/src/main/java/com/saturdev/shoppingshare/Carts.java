package com.saturdev.shoppingshare;

public class Carts {
    private String cart_name, cart_items, cart_description;
    private String cart_price;

    public Carts() {
    }

    public Carts(String cart_name, String cart_items, String cart_description, String cart_price) {
        this.cart_name = cart_name;
        this.cart_items = cart_items;
        this.cart_description = cart_description;
        this.cart_price = cart_price;
    }


    public String getCart_name() {
        return cart_name;
    }

    public void setCart_name(String cart_name) {
        this.cart_name = cart_name;
    }

    public String getCart_items() {
        return cart_items;
    }

    public void setCart_items(String cart_items) {
        this.cart_items = cart_items;
    }

    public String getCart_description() {
        return cart_description;
    }

    public void setCart_description(String cart_description) {
        this.cart_description = cart_description;
    }

    public String getCart_price() {
        return cart_price;
    }

    public void setCart_price(String cart_price) {
        this.cart_price = cart_price;
    }
}
