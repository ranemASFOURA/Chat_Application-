package org.example;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    private JButton signInButton, signUpButton; // Buttons to navigate to sign-in or sign-up screens

    // Constructor to initialize the Welcome Panel components
    public WelcomePanel() {
        setLayout(new GridLayout(3, 1, 10, 10)); // Use GridLayout for structured button placement

        // Create a label to welcome the user
        JLabel welcomeLabel = new JLabel("Welcome to the Chat Application!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Initialize buttons with action options
        signInButton = new JButton("Sign In");
        signUpButton = new JButton("Sign Up");

        // Add components to the panel for user interaction
        add(welcomeLabel);
        add(signInButton);
        add(signUpButton);
    }

    // Getter to access the sign-in button from other classes
    public JButton getSignInButton() {
        return signInButton;
    }

    // Getter to access the sign-up button from other classes
    public JButton getSignUpButton() {
        return signUpButton;
    }
}
