package org.example;

import org.example.network.BackendClient;
import org.example.network.ClientManager;
import org.example.network.JSONServer;
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

    private static ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    public static void main(String[] args) {
        JSONServer jsonServer = context.getBean(JSONServer.class);
        jsonServer.run();
    }
}
