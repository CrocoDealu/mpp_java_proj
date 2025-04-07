package org.example.network;

import java.io.IOException;

public class BackendConnectionManager {
    private static BackendClient client;

    public static BackendClient getClient() {
        if (client == null) {
            try {
                client = new BackendClient("localhost", 1234);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }
}
