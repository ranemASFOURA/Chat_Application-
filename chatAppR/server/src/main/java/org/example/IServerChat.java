package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

// This interface defines server-side operations for managing chat rooms and client interactions.
public interface IServerChat extends Remote {
    // Method to add a new chat room.
    public void addRoom(String roomName, String creatorUsername) throws RemoteException;

    // Method to remove an existing chat room.
    public void removeRoom(String roomName, String username) throws RemoteException;

    // Method to register a client with a username and password.
    void signIn(String username, String password, org.example.IClientChat client) throws RemoteException;

    public void signUp(String username, String password, String firstName, String lastName) throws RemoteException;

    // Method to allow a client to join a specific chat room.
    void signInRoom(String username, String roomName) throws RemoteException;

    // Method to log a client out.
    void signOut(String username) throws RemoteException;

    boolean login(String username, String password) throws RemoteException;

    // Method to retrieve a list of available chat rooms.
    List<String> showRooms() throws RemoteException;

    // Method to retrieve a list of clients in a specific chat room.
    List<String> showClientsInRoom(String roomName) throws RemoteException;

    // Method to send a message to all clients in a specific room or to selected clients.
    void broadcastMessage(String sender, String roomName, String message) throws RemoteException;

    void sendMessage(String sender, String roomName, String message, List<String> recipients) throws RemoteException;
    public List<String> getMissedMessages(String username) throws RemoteException;
    public List<String> getRoomHistory(String roomName)  throws RemoteException;
}

