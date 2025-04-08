package org.example.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientManager {
    private static final Set<BackendClient> connectedClients = Collections.synchronizedSet(new HashSet<>());;

    public static void addClient(BackendClient client) {
        connectedClients.add(client);
    }

    public static void removeClient(BackendClient client) {
        connectedClients.remove(client);
    }

    public static void broadcastChange(String changeNotification) {
        connectedClients.forEach(client -> {
            try {
                client.send(changeNotification);
            } catch (Exception e) {
                System.out.println("Failed to notify client: " + e.getMessage());
            }
        });
    }
}
