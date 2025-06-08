package com.example.myapplication.models;

import android.util.Log;

/**
 * Represents an item within a shopping cart.
 * Each CartItem consists of a {@link Crystal} object and the quantity of that crystal
 * selected by the user.
 *
 * This class is designed to be used with Firestore, hence the requirement for a
 * public no-argument constructor and public getter/setter methods for its properties.
 */
public class CartItem {
  private static final String TAG = "CartItem"; // Tag for logging

  // The Crystal object that this cart item represents.
  private Crystal crystal;
  // The quantity of the crystal in the cart.
  private int quantity;

  /**
   * Default no-argument constructor.
   * This is required by Firestore for deserializing objects from database documents.
   * It's good practice to leave this empty or with minimal default initializations
   * if Firestore is creating the object.
   */
  public CartItem() {
    // Required empty public constructor for Firestore deserialization.
  }

  /**
   * Constructs a new CartItem with the specified crystal and quantity.
   *
   * @param crystal The {@link Crystal} object for this cart item.
   *                It's logged if this parameter is null, as it's unexpected.
   * @param quantity The quantity of the crystal. Should be a positive integer.
   */
  public CartItem(Crystal crystal, int quantity) {
    // Basic null check for the crystal object, as a cart item without a crystal is invalid.
    if (crystal == null) {
      Log.e(TAG, "Crystal object is null in CartItem constructor!");
      // Depending on requirements, you might want to throw an IllegalArgumentException here
      // or handle it in a way that prevents an invalid state.
    }
    this.crystal = crystal;
    this.quantity = quantity;
  }

  /**
   * Gets the {@link Crystal} object associated with this cart item.
   *
   * @return The {@link Crystal} object.
   */
  public Crystal getCrystal() {
    return crystal;
  }

  /**
   * Sets the {@link Crystal} object for this cart item.
   * This method is primarily for Firestore's use during deserialization.
   *
   * @param crystal The new {@link Crystal} object to set.
   */
  public void setCrystal(Crystal crystal) {
    this.crystal = crystal;
  }

  /**
   * Gets the quantity of the crystal in this cart item.
   *
   * @return The quantity as an integer.
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * Sets the quantity of the crystal for this cart item.
   * This method is primarily for Firestore's use during deserialization
   * or when the quantity is updated directly.
   *
   * @param quantity The new quantity to set.
   */
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}