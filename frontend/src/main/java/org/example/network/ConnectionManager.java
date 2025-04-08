package org.example.network;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionManager {
    private static FrontendClient client;
    private static ResponseParser responseParser = new ResponseParser();
    private static JSONDispatcher dispatcher = new JSONDispatcher();
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public static FrontendClient getClient() {
        if (client == null || client.isClosed()) {
            try {
                client = new FrontendClient("localhost", 1234, dispatcher);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public static ResponseParser getResponseParser() {
        return responseParser;
    }

    public static JSONDispatcher getDispatcher() {
        return dispatcher;
    }

    public static int getNextMessageId() {
        return idGenerator.getAndIncrement();
    }
}
