package org.example.utils;

import org.example.controller.LoginController;
import org.example.controller.MainController;
import org.example.controller.TicketsController;
import org.example.repository.CashierDBRepository;
import org.example.repository.GameDBRepository;
import org.example.repository.TicketDBRepository;
import org.example.service.CashierService;
import org.example.service.GameService;
import org.example.service.TicketService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig {

    @Bean
    public Properties properties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Bean
    public JdbcUtils jdbcUtils(Properties properties) {
        return new JdbcUtils(properties);
    }

    @Bean
    public GameDBRepository gameDBRepository(JdbcUtils jdbcUtils) {
        return new GameDBRepository(jdbcUtils);
    }

    @Bean
    public GameService gameService(GameDBRepository gameDBRepository) {
        return new GameService(gameDBRepository);
    }

    @Bean
    public CashierDBRepository cashierDBRepository(JdbcUtils jdbcUtils) {
        return new CashierDBRepository(jdbcUtils);
    }

    @Bean
    public CashierService cashierService(CashierDBRepository cashierDBRepository) {
        return new CashierService(cashierDBRepository);
    }

    @Bean
    public TicketDBRepository ticketDBRepository(JdbcUtils jdbcUtils, GameDBRepository gameDBRepository, CashierDBRepository cashierDBRepository) {
        return new TicketDBRepository(jdbcUtils, gameDBRepository, cashierDBRepository);
    }

    @Bean
    public TicketService ticketService(TicketDBRepository ticketDBRepository) {
        return new TicketService(ticketDBRepository);
    }

    @Bean
    public LoginController loginController(GameService gameService, CashierService cashierService, TicketService ticketService, SpringFXMLLoader loader) {
        return new LoginController(gameService, cashierService, ticketService, loader);
    }

    @Bean
    public MainController mainController(GameService gameService, CashierService cashierService, TicketService ticketService, SpringFXMLLoader loader) {
        return new MainController(gameService, cashierService, ticketService, loader);
    }

    @Bean
    public TicketsController ticketsController(TicketService ticketService) {
        return new TicketsController(ticketService);
    }
}
