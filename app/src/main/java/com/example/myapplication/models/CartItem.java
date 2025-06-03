package com.example.myapplication.models;
import android.util.Log;

public class CartItem {
  private Crystal crystal;
  private int quantity;

  public CartItem() {
    // Required empty constructor for Firestore
  }

  public CartItem(Crystal crystal, int quantity) {
    if (crystal == null) {
      Log.e("CartItem", "Crystal is null in CartItem constructor!");
    }
    this.crystal = crystal;
    this.quantity = quantity;
  }

  public Crystal getCrystal() {
    return crystal;
  }

  public void setCrystal(Crystal crystal) {
    this.crystal = crystal;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
