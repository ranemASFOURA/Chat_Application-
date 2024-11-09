package org.example;

import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;

public class ChatApplication {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private static IServerChat serverChat; // Keep a persistent connection to the server

    public ChatApplication() {
        frame = new JFrame("Chat Application");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        WelcomePanel welcomePanel = new WelcomePanel();
        SignInPanel signInPanel = new SignInPanel();
        SignUpPanel signUpPanel = new SignUpPanel();
        ServicePanel servicePanel = new ServicePanel();

        // Add panels to main panel
        mainPanel.add(welcomePanel, "Welcome");
        mainPanel.add(signInPanel, "SignIn");
        mainPanel.add(signUpPanel, "SignUp");
        mainPanel.add(servicePanel, "Service");

        // Add action listeners to switch between panels
        welcomePanel.getSignInButton().addActionListener(e -> cardLayout.show(mainPanel, "SignIn"));
        welcomePanel.getSignUpButton().addActionListener(e -> cardLayout.show(mainPanel, "SignUp"));

        // Attempt to connect to the server once when the app starts
        try {
            serverChat = (IServerChat) Naming.lookup("rmi://localhost:5099/ChatService");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to connect to server.");
            System.exit(1); // Exit if unable to connect
        }

        // Add action listener to sign-in button in SignInPanel
        signInPanel.getSignInButton().addActionListener(e -> {
            String username = signInPanel.getUsername();
            String password = signInPanel.getPassword();
            try {
                if (serverChat.login(username, password)) {
                    JOptionPane.showMessageDialog(null, "Sign-in successful");
                    servicePanel.setCurrentUsername(username); // Set current username
                    cardLayout.show(mainPanel, "Service");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error during sign-in");
            }
        });

        // Set up the frame
        frame.add(mainPanel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatApplication::new);
    }

    public static IServerChat getServerChat() {
        return serverChat;
    }
}
