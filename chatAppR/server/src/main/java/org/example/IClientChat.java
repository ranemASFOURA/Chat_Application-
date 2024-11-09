package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

// This interface defines the client-side operations for receiving messages.
public interface IClientChat extends Remote {
    // Method to receive a message sent by the server.
    void receiveMessage(String sender, String roomName, String message) throws RemoteException;
    String getUsername() throws RemoteException;

}
