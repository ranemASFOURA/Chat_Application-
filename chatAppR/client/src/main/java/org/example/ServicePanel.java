package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

public class ServicePanel extends JPanel {
    private JButton addRoomButton, removeRoomButton, showRoomsButton, showClientsButton, sendMessageButton, signInRoomButton, signOutButton, getRoomHistoryButton;
    private String currentUsername;  // Tracking the current user's username

    public ServicePanel() {
        setLayout(new GridLayout(8, 1, 10, 10));  // Update layout to accommodate new button

        // Initialize buttons
        addRoomButton = new JButton("Add Chat Room");
        removeRoomButton = new JButton("Remove Chat Room");
        showRoomsButton = new JButton("Show Chat Rooms");
        showClientsButton = new JButton("Show Clients in Room");
        sendMessageButton = new JButton("Send Message");
        signInRoomButton = new JButton("Sign In Room");
        signOutButton = new JButton("Sign Out");
        getRoomHistoryButton = new JButton("Get Room History");  // New button

        // Add buttons to panel
        add(addRoomButton);
        add(removeRoomButton);
        add(showRoomsButton);
        add(showClientsButton);
        add(sendMessageButton);
        add(signInRoomButton);
        add(signOutButton);
        add(getRoomHistoryButton);  // Add new button to panel

        // Event handler for "Get Room History"
        getRoomHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = JOptionPane.showInputDialog("Enter room name to view history:");
                if (roomName != null && !roomName.trim().isEmpty()) {
                    try {
                        List<String> roomHistory = ChatApplication.getServerChat().getRoomHistory(roomName);
                        if (roomHistory.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No message history for room " + roomName + ".");
                        } else {
                            JOptionPane.showMessageDialog(null, "Room History for " + roomName + ":\n" + String.join("\n", roomHistory));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error fetching room history.");
                    }
                }
            }
        });

        // Event handler for "Add Chat Room"
        addRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = JOptionPane.showInputDialog("Enter room name:");
                if (roomName != null && !roomName.trim().isEmpty()) {
                    try {
                        ChatApplication.getServerChat().addRoom(roomName, currentUsername);
                        if (currentUsername != null) {
                            ChatApplication.getServerChat().signInRoom(currentUsername, roomName); // Add the creator to the room
                        }
                        JOptionPane.showMessageDialog(null, "Room added and you have joined the room.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error adding room.");
                    }
                }
            }
        });

        // Event handler for "Remove Chat Room"
        removeRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = JOptionPane.showInputDialog("Enter room name to remove:");
                if (roomName != null && !roomName.trim().isEmpty()) {
                    try {
                        ChatApplication.getServerChat().removeRoom(roomName, currentUsername);
                        JOptionPane.showMessageDialog(null, "Room removed successfully.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error removing room.");
                    }
                }
            }
        });

        // Event handler for "Show Chat Rooms"
        showRoomsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<String> rooms = ChatApplication.getServerChat().showRooms();
                    JOptionPane.showMessageDialog(null, "Available Rooms: " + rooms);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error fetching rooms from server.");
                }
            }
        });

        // Event handler for "Show Clients in Room"
        showClientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = JOptionPane.showInputDialog("Enter room name to show clients:");
                if (roomName != null && !roomName.trim().isEmpty()) {
                    try {
                        List<String> clients = ChatApplication.getServerChat().showClientsInRoom(roomName);
                        JOptionPane.showMessageDialog(null, "Clients in Room " + roomName + ": " + clients);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error fetching clients.");
                    }
                }
            }
        });

        // Event handler for "Send Message"
        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prompt user to enter the room name where the message should be sent
                String roomName = JOptionPane.showInputDialog("Enter room name:");
                // Prompt user to enter the message content
                String message = JOptionPane.showInputDialog("Enter message:");

                // Ensure that both room name and message are not empty or null
                if (roomName != null && message != null && !roomName.trim().isEmpty() && !message.trim().isEmpty()) {
                    try {
                        // Define the sender of the message using the current user's username
                        String sender = currentUsername;

                        // Prompt user to specify recipients for the message (comma-separated)
                        String recipients = JOptionPane.showInputDialog("Enter recipients (comma-separated) or leave empty to broadcast:");
                        List<String> recipientList = null;

                        // If recipients are specified, convert them to a list
                        if (recipients != null && !recipients.trim().isEmpty()) {
                            recipientList = List.of(recipients.split(","));
                        }

                        // Call the appropriate method based on the presence of recipients
                        if (recipientList == null || recipientList.isEmpty()) {
                            // If no specific recipients are provided, broadcast the message to all users in the room
                            ChatApplication.getServerChat().broadcastMessage(sender, roomName, message);
                            JOptionPane.showMessageDialog(null, "Your message has been successfully broadcasted to all users in the room " + roomName);
                        } else {
                            // If recipients are specified, send the message to those recipients only
                            ChatApplication.getServerChat().sendMessage(sender, roomName, message, recipientList);
                            JOptionPane.showMessageDialog(null, "Your message has been successfully sent to selected recipients.");
                        }
                    } catch (Exception ex) {
                        // Print any exceptions to the console and show an error dialog to the user
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error sending message.");
                    }
                }
            }
        });

        // Event handler for "Sign In Room"
        signInRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentUsername == null) {
                    JOptionPane.showMessageDialog(null, "You need to sign in first.");
                    return;
                }
                String roomName = JOptionPane.showInputDialog("Enter room name to join:");
                if (roomName != null && !roomName.trim().isEmpty()) {
                    try {
                        ChatApplication.getServerChat().signInRoom(currentUsername, roomName);
                        JOptionPane.showMessageDialog(null, "Signed into room: " + roomName);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error signing into room.");
                    }
                }
            }
        });

        // Event handler for "Sign Out"
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (currentUsername != null) {
                        ChatApplication.getServerChat().signOut(currentUsername);
                        JOptionPane.showMessageDialog(null, "Signed out successfully.");
                        currentUsername = null;  // Reset current username
                    } else {
                        JOptionPane.showMessageDialog(null, "You are not signed in.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error signing out.");
                }
            }
        });

    }

    // Method to set the current username
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
}
