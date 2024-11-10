package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpPanel extends JPanel {
    private JTextField usernameField, firstNameField, lastNameField;
    private JPasswordField passwordField;
    private JButton signUpButton;

    public SignUpPanel() {
        setLayout(new GridLayout(5, 2, 10, 10)); // Grid layout with 5 rows

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel firstNameLabel = new JLabel("First Name:");
        JLabel lastNameLabel = new JLabel("Last Name:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        signUpButton = new JButton("Sign Up");

        // Add components to the panel
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(firstNameLabel);
        add(firstNameField);
        add(lastNameLabel);
        add(lastNameField);
        add(new JLabel()); // Empty space
        add(signUpButton);

        // Action listener for the sign-up button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Collect input data
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();

                try {
                    // Use persistent serverChat instance for sign-up
                    IServerChat serverChat = ChatApplication.getServerChat();
                    serverChat.signUp(username, password, firstName, lastName);
                    JOptionPane.showMessageDialog(null, "Sign Up successful!");
                    ((CardLayout) getParent().getLayout()).show(getParent(), "SignIn");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error connecting to server.");
                }
            }
        });
    }
}
