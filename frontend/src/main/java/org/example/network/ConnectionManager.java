package org.example.network;

import java.io.IOException;

public class ConnectionManager {
    private static FrontendClient client;
    private static ResponseParser responseParser = new ResponseParser();

    public static FrontendClient getClient() {
        if (client == null || client.isClosed()) {
            try {
                client = new FrontendClient("localhost", 1234, responseParser);
                client.startListening();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public static ResponseParser getResponseParser() {
        return responseParser;
    }
}
