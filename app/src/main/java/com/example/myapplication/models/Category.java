package com.example.myapplication.models;

/**
 * Represents a category for classifying crystals.
 * Each category has a name and an associated image resource ID.
 * This class is a simple Plain Old Java Object (POJO) used to model category data.
 */
public class Category {
    // The name of the category (e.g., "Healing Stones", "Decorative Crystals").
    private String name;
    // The resource ID for the image representing this category.
    // This would typically be an ID from R.drawable.
    private int imageResId;

    /**
     * Constructs a new Category object.
     *
     * @param name The name of the category.
     * @param imageResId The drawable resource ID for the category's image.
     */
    public Category(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    /**
     * Gets the name of the category.
     *
     * @return The name of the category as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the image resource ID for the category.
     *
     * @return The integer resource ID for the category's image.
     */
    public int getImageResId() {
        return imageResId;
    }

    // No setters are provided, making Category objects immutable after creation.
    // If mutability is required (e.g., for Firestore deserialization without a custom deserializer,
    // or for direct modification after instantiation), public setters would be needed.
    // For Firestore, if this class is directly deserialized, a public no-argument constructor
    // and public setters for 'name' and 'imageResId' would typically be required unless
    // a custom deserialization logic is in place.
}