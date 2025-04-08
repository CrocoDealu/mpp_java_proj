package org.example;

import org.example.network.BackendClient;
import org.example.network.ClientManager;
import org.example.network.RequestHandler;
import org.example.repository.GameDBRepository;
import org.example.service.SportsTicketManagementService;
import org.example.utils.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main{

    private static ApplicationContext springContext;

    public static void main(String[] args) {
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);
        ExecutorService executor = Executors.newFixedThreadPool(10); // Handle up to 10 clients

        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server is listening on port 1234");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                executor.execute(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    public static void handleClient(Socket socket) {
        try {
            BackendClient backendClient = new BackendClient(socket);
            ClientManager.addClient(backendClient);
            RequestHandler requestHandler = new RequestHandler(springContext.getBean(SportsTicketManagementService.class));

            while (!backendClient.isClosed()) {
                try {
                    String request = backendClient.receive();
                    if (request == null) {
                        System.out.println("Client disconnected");
                        ClientManager.removeClient(backendClient);
                        break;
                    }
                    requestHandler.handleRequest(request, backendClient);
                } catch (IOException e) {
                    System.out.println("Client connection lost: " + e.getMessage());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }
}
