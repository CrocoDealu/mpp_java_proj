package org.example.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BackendClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public BackendClient(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public synchronized void send(String requestJson) {
        out.println(requestJson);
    }

    public String receive() throws IOException {
        try {
            if (socket.isClosed() || !socket.isConnected()) {
                return null;
            }
            return in.readLine();
        } catch (IOException e) {
            close();
            return null;
        }
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
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public boolean isClosed() {
        return socket.isClosed();
    }
}
