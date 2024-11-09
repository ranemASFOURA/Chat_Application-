package org.example;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            System.setProperty("java.security.policy", "server.policy"); // Adjust policy file path as needed
            ServerChatImpl serverChat = new ServerChatImpl();
            Registry registry = LocateRegistry.createRegistry(5099);
            registry.bind("ChatService", serverChat);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
