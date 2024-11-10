package org.example;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

public class ServerChatImpl extends UnicastRemoteObject implements IServerChat {

    // Mapping of room names to their list of clients (users in each room)
    private Map<String, List<IClientChat>> chatRooms;
    // Mapping of usernames to passwords for authentication
    private Map<String, String> users;
    // Mapping of logged-in usernames to their client objects
    private Map<String, IClientChat> loggedInUsers;
    // Mapping of room names to their creators (room owners)
    private Map<String, String> roomOwners;
    // Mapping of users to their unread messages (missed messages)
    private Map<String, List<String>> missedMessages;
    private Map<String, List<String>> roomUsernames;

    // New: Map to store room history (chat history for each room)
    private Map<String, List<String>> roomHistories;

    // Constructor to initialize the server implementation
    public ServerChatImpl() throws RemoteException {
        super();
        chatRooms = new HashMap<>();
        users = new HashMap<>();
        loggedInUsers = new HashMap<>();
        roomOwners = new HashMap<>();
        missedMessages = new HashMap<>();
        roomUsernames = new HashMap<>();
        // Initialize the room history map to store chat history for each room
        roomHistories = new HashMap<>();
    }

    // Method to validate user login with a username and password
    @Override
    public boolean login(String username, String password) throws RemoteException {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    // Method to add a new chat room
    @Override
    public void addRoom(String roomName, String creatorUsername) throws RemoteException {
        // Check if the room already exists
        if (!chatRooms.containsKey(roomName)) {
            chatRooms.put(roomName, new ArrayList<>());
            roomOwners.put(roomName, creatorUsername);
            roomUsernames.put(roomName, new ArrayList<>());

            // Initialize history list for the new room
            roomHistories.put(roomName, new ArrayList<>());
            System.out.println("Room added: " + roomName + " by " + creatorUsername);
        } else {
            System.out.println("Room already exists: " + roomName);
        }
    }

    // Method to remove an existing chat room
    @Override
    public boolean removeRoom(String roomName, String username) throws RemoteException {
        // Check if the room exists
        if (chatRooms.containsKey(roomName)) {
            // Verify that the requesting user is the creator of the room
            if (roomOwners.get(roomName).equals(username)) {
                chatRooms.remove(roomName);
                roomOwners.remove(roomName);
                System.out.println("Room removed: " + roomName + " by " + username);
                return true; // Operation successful
            } else {
                // If the user is not the creator of the room
                System.out.println("User " + username + " is not allowed to remove room " + roomName);
                return false; // Operation unsuccessful
            }
        } else {
            System.out.println("Room does not exist: " + roomName);
            return false; // Room does not exist
        }
    }



    // Method to sign in a user, validating their username and password
    @Override
    public void signIn(String username, String password, IClientChat client) throws RemoteException {
        if (!users.containsKey(username)) {
            throw new RemoteException("User not registered");
        }
        if (!users.get(username).equals(password)) {
            throw new RemoteException("Incorrect password");
        }

        // Add user to the list of logged-in users
        loggedInUsers.put(username, client);
        System.out.println("User signed in: " + username);

        // If the user has missed messages, send them now
        if (missedMessages.containsKey(username)) {
            List<String> messages = missedMessages.get(username);
            for (String message : messages) {
                client.receiveMessage("Server", "General", message);
            }
            missedMessages.remove(username);  // Remove the missed messages after sending
        }
    }

    // Method to register a new user
    @Override
    public void signUp(String username, String password, String firstName, String lastName) throws RemoteException {
        // Check if the username already exists
        if (users.containsKey(username)) {
            throw new RemoteException("Username already exists");
        }
        // Store the user's credentials and details
        users.put(username, password);
        System.out.println("User signed up: " + username + " (" + firstName + " " + lastName + ")");
    }

    // Method for a user to sign into a chat room
    @Override
    public void signInRoom(String username, String roomName) throws RemoteException {
        if (!chatRooms.containsKey(roomName)) {
            throw new RemoteException("Room does not exist");
        }

        IClientChat client = loggedInUsers.get(username);

        if (client != null) {
            // Add the user to the room's list of clients
            chatRooms.get(roomName).add(client);
        }

        // Add the username to the list of users in the room
        if (!roomUsernames.get(roomName).contains(username)) {
            roomUsernames.get(roomName).add(username);
        }

        System.out.println("User " + username + " joined room: " + roomName);
    }

    // Method to sign out a user and remove them from the logged-in users list
    @Override
    public void signOut(String username) throws RemoteException {
        // Remove the user from the logged-in users list
        loggedInUsers.remove(username);

        // Save missed messages for the user if they sign out
        if (!missedMessages.containsKey(username)) {
            missedMessages.put(username, new ArrayList<>());
        }

        System.out.println("User signed out: " + username);
    }

    // Method to retrieve a list of available chat rooms
    @Override
    public List<String> showRooms() throws RemoteException {
        return new ArrayList<>(chatRooms.keySet());
    }

    // Method to retrieve a list of clients (users) in a specific chat room
    @Override
    public List<String> showClientsInRoom(String roomName) throws RemoteException {
        if (!roomUsernames.containsKey(roomName)) {
            throw new RemoteException("Room not found: " + roomName);
        }

        return new ArrayList<>(roomUsernames.get(roomName));
    }

    // Method to send a message to a specific list of recipients in a room
    @Override
    public void sendMessage(String sender, String roomName, String message, List<String> recipients) throws RemoteException {
        if (chatRooms.containsKey(roomName)) {
            List<IClientChat> clients = chatRooms.get(roomName);
            String fullMessage = sender + ": " + message;

            // Store the message in the room's history
            roomHistories.get(roomName).add(fullMessage);

            for (String recipient : recipients) {
                boolean messageSent = false;

                // Try to send the message to each recipient
                for (IClientChat client : clients) {
                    if (client.getUsername().equals(recipient)) {
                        try {
                            client.receiveMessage(sender, roomName, message);
                            messageSent = true;
                            System.out.println("Message from " + sender + " to " + recipient + ": " + message);
                        } catch (RemoteException e) {
                            if (!messageSent) {
                                // Save message if the recipient is offline
                                missedMessages.computeIfAbsent(recipient, k -> new ArrayList<>()).add("From " + sender + ": " + message);
                                System.out.println("User " + recipient + " is offline. Saving message for later.");
                            }
                        }
                    }
                }
            }
        } else {
            throw new RemoteException("Room not found: " + roomName);
        }
    }

    // Method to retrieve missed messages for a user
    @Override
    public List<String> getMissedMessages(String username) throws RemoteException {
        if (missedMessages.containsKey(username)) {
            return missedMessages.get(username);
        }
        return new ArrayList<>();  // Return empty if no missed messages
    }

    // Method to send a broadcast message to all users in a room except the sender
    @Override
    public void broadcastMessage(String sender, String roomName, String message) throws RemoteException {
        if (chatRooms.containsKey(roomName)) {
            List<IClientChat> clients = chatRooms.get(roomName);
            String fullMessage = sender + ": " + message;

            // Store the broadcast message in the room's history
            roomHistories.get(roomName).add(fullMessage);

            for (IClientChat client : clients) {
                try {
                    if (!client.getUsername().equals(sender)) {
                        client.receiveMessage(sender, roomName, message);
                        System.out.println("Broadcast message from " + sender + " to " + client.getUsername() + ": " + message);
                    }
                } catch (RemoteException e) {
                    // Save message for later if the recipient is offline
                    missedMessages.computeIfAbsent(client.getUsername(), k -> new ArrayList<>()).add("Broadcast from " + sender + ": " + message);
                    System.out.println("Saved unread broadcast message for " + client.getUsername() + " as they are offline.");
                }
            }
        } else {
            throw new RemoteException("Room not found: " + roomName);
        }
    }

    // New method to get the chat history for a specific room
    @Override
    public List<String> getRoomHistory(String roomName) throws RemoteException {
        return roomHistories.getOrDefault(roomName, Collections.emptyList());
    }
}
