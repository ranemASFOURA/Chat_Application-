package org.example;

import javax.swing.*;
import java.awt.*;

public class SignInPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signInButton;

    public SignInPanel() {
        setLayout(new GridLayout(3, 2, 10, 10)); // Grid layout with spacing

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        signInButton = new JButton("Sign In");

        // Add components to the panel
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Empty space for layout alignment
        add(signInButton);
    }

    // Getter methods to access username and password
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // Getter for the sign-in button to add action listeners from outside if needed
    public JButton getSignInButton() {
        return signInButton;
    }
}
