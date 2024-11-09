package org.example;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientChatImpl extends UnicastRemoteObject implements IClientChat {

    private String username;

    public ClientChatImpl(String username) throws RemoteException {
        this.username = username;
    }

    @Override
    public void receiveMessage(String sender, String roomName, String message) throws RemoteException {
        // Display message to the client in a graphical message dialog
        JOptionPane.showMessageDialog(null,
                "Message from " + sender + " in room " + roomName + ": " + message,
                "New Message",
                JOptionPane.INFORMATION_MESSAGE);
        // Alternatively, print the message in the console
        System.out.println("Message from " + sender + " in room " + roomName + ": " + message);
    }

    @Override
    public String getUsername() {
        return username;
    }

}
