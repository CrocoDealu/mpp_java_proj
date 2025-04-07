package org.example.network;

import java.io.IOException;

public class ConnectionManager {
    private static FrontendClient client;

    public static FrontendClient getClient() {
        if (client == null || client.isClosed()) {
            try {
                client = new FrontendClient("localhost", 1234);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }
}
