package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AuthActivity serves as the entry point for user authentication.
 * It provides options for users to either log in to an existing account
 * or register for a new account. This activity typically appears when
 * the application starts and no user is currently signed in, or when
 * a user explicitly logs out.
 */
public class AuthActivity extends AppCompatActivity {

  /**
   * Called when the activity is first created.
   * This method is responsible for setting up the layout of the activity
   * and initializing the buttons for login and registration.
   *
   * @param savedInstanceState If the activity is being re-initialized after
   *                           previously being shut down then this Bundle contains the data it most
   *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Set the user interface layout for this activity.
    // The layout file is defined in res/layout/activity_auth.xml
    setContentView(R.layout.activity_auth);

    // Initialize the interactive elements on the screen.
    initButtons();
  }

  /**
   * Initializes the login and register buttons.
   * This method finds the button views by their IDs from the layout
   * and sets up OnClickListeners to handle user interactions.
   * Clicking the login button will navigate to the LoginActivity.
   * Clicking the register button will navigate to the RegistrationActivity.
   */
  private void initButtons() {
    // Find the login button view from the layout.
    Button loginButton = findViewById(R.id.loginButton);
    // Find the register button view from the layout.
    Button registerButton = findViewById(R.id.registerButton);

    // Set a click listener for the login button.
    // When clicked, it will call the openLogin() method.
    loginButton.setOnClickListener(v -> openLogin());

    // Set a click listener for the register button.
    // When clicked, it will call the openRegister() method.
    registerButton.setOnClickListener(v -> openRegister());
  }

  /**
   * Navigates the user to the LoginActivity.
   * This method creates an Intent to start the LoginActivity,
   * allowing the user to enter their credentials.
   */
  private void openLogin() {
    // Create an Intent to start LoginActivity.
    // 'this' refers to the current activity (AuthActivity) context.
    // LoginActivity.class is the target activity to be started.
    startActivity(new Intent(this, LoginActivity.class));
  }

  /**
   * Navigates the user to the RegistrationActivity.
   * This method creates an Intent to start the RegistrationActivity,
   * allowing the user to create a new account.
   */
  private void openRegister() {
    // Create an Intent to start RegistrationActivity.
    // 'this' refers to the current activity (AuthActivity) context.
    // RegistrationActivity.class is the target activity to be started.
    startActivity(new Intent(this, RegistrationActivity.class));
  }
}