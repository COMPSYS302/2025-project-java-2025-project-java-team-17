package com.example.myapplication.models;

import java.util.List;

public class Crystal {
    private String id;
    private String name;
    private String description;
    private String category;
    private List<String> tags;
    private List<String> imageUrls;
    private double price;
    private int stock;
    private int views;

    public Crystal() {
        // Required by Firestore
    }

    public Crystal(String id, String name, String description, String category,
                   List<String> tags, List<String> imageUrls,
                   double price, int stock, int views) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.imageUrls = imageUrls;
        this.price = price;
        this.stock = stock;
        this.views = views;
    }

    // Getters (you can add setters too if needed)

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public List<String> getTags() { return tags; }
    public List<String> getImageUrls() { return imageUrls; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getViews() { return views; }
}