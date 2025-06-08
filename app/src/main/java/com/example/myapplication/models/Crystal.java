package com.example.myapplication.models;

import java.util.List;

/**
 * Represents a crystal product.
 * This class holds detailed information about a crystal, including its ID, name,
 * description, category, tags, image URLs, price, stock level, and view count.
 *
 * It is designed for use with Firestore, hence the public no-argument constructor
 * and public getter methods for all properties. Setters can be added if direct
 * modification of Crystal objects after creation is required, or if Firestore needs
 * them for deserialization in certain scenarios (though often getters and a
 * constructor are sufficient if field names match).
 */
public class Crystal {
    // Unique identifier for the crystal. Often corresponds to the Firestore document ID.
    private String id;
    // The common name of the crystal (e.g., "Amethyst Geode", "Rose Quartz").
    private String name;
    // A detailed description of the crystal, its properties, or origins.
    private String description;
    // The category this crystal belongs to (e.g., "Healing", "Decorative", "Rare").
    private String category;
    // A list of tags associated with the crystal, useful for searching or filtering
    // (e.g., "calming", "protection", "meditation").
    private List<String> tags;
    // A list of URLs pointing to images of the crystal.
    private List<String> imageUrls;
    // The price of the crystal.
    private double price;
    // The current stock quantity available for this crystal.
    private int stock;
    // A counter for how many times the crystal's detail page has been viewed.
    private int views;

    /**
     * Default no-argument constructor.
     * This is required by Firestore for deserializing objects from database documents.
     * It's good practice to leave this empty or with minimal default initializations
     * if Firestore is creating the object.
     */
    public Crystal() {
        // Required by Firestore for data mapping.
    }

    /**
     * Constructs a new Crystal object with all its properties initialized.
     *
     * @param id          The unique identifier for the crystal.
     * @param name        The name of the crystal.
     * @param description A description of the crystal.
     * @param category    The category the crystal belongs to.
     * @param tags        A list of tags associated with the crystal.
     * @param imageUrls   A list of image URLs for the crystal.
     * @param price       The price of the crystal.
     * @param stock       The current stock quantity.
     * @param views       The number of times the crystal has been viewed.
     */
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

    // --- Getters ---
    // (Setters can be added if needed for mutability or specific Firestore use cases)

    /**
     * Gets the unique identifier of the crystal.
     * @return The ID string.
     */
    public String getId() { return id; }

    /**
     * Gets the name of the crystal.
     * @return The name string.
     */
    public String getName() { return name; }

    /**
     * Gets the description of the crystal.
     * @return The description string.
     */
    public String getDescription() { return description; }

    /**
     * Gets the category of the crystal.
     * @return The category string.
     */
    public String getCategory() { return category; }

    /**
     * Gets the list of tags associated with the crystal.
     * @return A List of tag strings.
     */
    public List<String> getTags() { return tags; }

    /**
     * Gets the list of image URLs for the crystal.
     * @return A List of image URL strings.
     */
    public List<String> getImageUrls() { return imageUrls; }

    /**
     * Gets the price of the crystal.
     * @return The price as a double.
     */
    public double getPrice() { return price; }

    /**
     * Gets the current stock quantity of the crystal.
     * @return The stock count as an integer.
     */
    public int getStock() { return stock; }

    /**
     * Gets the number of views for the crystal.
     * @return The view count as an integer.
     */
    public int getViews() { return views; }

    // --- Potential Setters (Uncomment or add if needed) ---
    // public void setId(String id) { this.id = id; }
    // public void setName(String name) { this.name = name; }
    // public void setDescription(String description) { this.description = description; }
    // public void setCategory(String category) { this.category = category; }
    // public void setTags(List<String> tags) { this.tags = tags; }
    // public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    // public void setPrice(double price) { this.price = price; }
    // public void setStock(int stock) { this.stock = stock; }
    // public void setViews(int views) { this.views = views; }
}