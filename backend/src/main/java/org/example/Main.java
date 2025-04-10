package org.example;

import org.example.network.BackendClient;
import org.example.network.ClientManager;
import org.example.network.RequestHandler;
import org.example.service.SportsTicketManagementService;
import org.example.utils.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main{

    private static ApplicationContext springContext;

    public static void main(String[] args) {
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);
        ExecutorService executor = Executors.newFixedThreadPool(10);

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
            SportsTicketManagementService sportsTicketManagementService = springContext.getBean(SportsTicketManagementService.class);
            sportsTicketManagementService.loginClient(backendClient);
            RequestHandler requestHandler = springContext.getBean(RequestHandler.class);

            while (!backendClient.isClosed()) {
                try {
                    String request = backendClient.receive();
                    if (request == null) {
                        System.out.println("Client disconnected");
                        sportsTicketManagementService.logoutClient(backendClient);
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
