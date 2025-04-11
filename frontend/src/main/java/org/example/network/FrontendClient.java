package org.example.network;

import javafx.application.Platform;
import org.example.util.Listener;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FrontendClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final List<Listener> listeners = new ArrayList<>();
    private Thread listenerThread;
    private JSONDispatcher dispatcher;

    public FrontendClient(String host, int port, JSONDispatcher dispatcher) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        startListenerThread();
        this.dispatcher = dispatcher;
    }

    private void startListenerThread() {
        listenerThread = new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                   String jsonString = in.readLine();
                    if (jsonString == null) {
                        System.out.println("Connection closed");
                        break;
                    }
                   if (!jsonString.isEmpty()) {
                       JSONObject message = new JSONObject(jsonString);
                       Platform.runLater(() -> dispatcher.dispatch(message));
                   }
                } catch (IOException e) {
                    System.out.println("Error in listener thread: " + e.getMessage());
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void send(String requestJson) {
        out.println(requestJson);
    }

    public void close() throws IOException {
        if (listenerThread != null) {
            try {
                listenerThread.join(1000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for listener thread to stop");
            }
        }
        
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
            listeners.clear();
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
