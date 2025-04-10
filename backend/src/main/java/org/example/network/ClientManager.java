package org.example.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClientManager implements Publisher<BackendClient> {
    private static final Set<BackendClient> connectedClients = Collections.synchronizedSet(new HashSet<>());;

    @Override
    public void subscribe(BackendClient client) {
        connectedClients.add(client);
    }

    @Override
    public void unsubscribe(BackendClient client) {
        connectedClients.remove(client);
    }

    @Override
    public void notifySubscribers(String changeNotification) {
        connectedClients.forEach(client -> {
            try {
                client.onNotify(changeNotification);
            } catch (Exception e) {
                System.out.println("Failed to notify client: " + e.getMessage());
            }
        });
    }
}
