package org.example.network;

import org.example.util.Listener;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class FrontendClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final List<Listener> listeners = new ArrayList<>();
    private ConcurrentHashMap<Integer, BlockingQueue<String>> responseQueues = new ConcurrentHashMap<>();
    private final BlockingQueue<String> notificationQueue = new LinkedBlockingQueue<>();
    private final AtomicInteger messageIdGenerator = new AtomicInteger(0);
    private final ResponseParser responseParser;

    public FrontendClient(String host, int port, ResponseParser responseParser) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        this.responseParser = responseParser;
    }

    public void send(String requestJson) {
        out.println(requestJson);
    }

    public String sendAndWaitResponse(JSONObject request) throws InterruptedException {
        int messageId = messageIdGenerator.incrementAndGet();
        request.put("messageId", messageId);
        BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>(1);
        responseQueues.put(messageId, responseQueue);

        send(request.toString());
        try {
            return responseQueue.take();
        } finally {
            responseQueues.remove(messageId);
            processQueuedNotifications();
        }
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
            responseQueues.clear();
            notificationQueue.clear();
            listeners.clear();
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void startListening() {
        new Thread(() -> {
            while (!isClosed()) {
                try {
                    String message = receive();
                    if (message != null) {
                        handleMessage(message);
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }, "MessageListener").start();
    }

    private void handleMessage(String message) {
        JSONObject json = new JSONObject(message);
        System.out.println(json);
        if (json.has("messageId")) {
            int messageId = json.getInt("messageId");
            BlockingQueue<String> responseQueue = responseQueues.get(messageId);
            if (responseQueue != null) {
                responseQueue.offer(message);
            }
        } else {
            notificationQueue.offer(message);
        }
    }

    private void processQueuedNotifications() {
        while (!notificationQueue.isEmpty()) {
            String notification = notificationQueue.poll();
            if (notification != null) {
                notifyListeners(notification);
            }
        }
    }

    public void addListener(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String notification) {
        JSONObject json = new JSONObject(notification);
        String type = json.getString("type");
        System.out.println(type);
        for (Listener listener : listeners) {
            System.out.println(listener);
            listener.onUpdate(type);
        }
    }
}
