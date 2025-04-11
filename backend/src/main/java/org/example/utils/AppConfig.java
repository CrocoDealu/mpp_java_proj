package org.example.utils;

import org.example.network.ClientManager;
import org.example.network.JSONServer;
import org.example.network.RequestHandler;
import org.example.repository.*;
import org.example.service.SportsTicketManagementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.util.Objects;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig {

    @Bean
    public Properties properties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(Objects.requireNonNull(getClass().getClassLoader().getResource("config.properties")).getFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Bean
    public ClientManager clientManager() {
        return new ClientManager();
    }

    @Bean
    public RequestHandler requestHandler(SportsTicketManagementService sportsTicketManagementService) {
        return new RequestHandler(sportsTicketManagementService);
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
    public SportsTicketManagementService sportsTicketManagementService(GameRepository gameRepository, TicketRepository ticketRepository, CashierRepository cashierRepository, ClientManager clientManager) {
        return new SportsTicketManagementService(cashierRepository, gameRepository, ticketRepository, clientManager);
    }

    @Bean
    public CashierDBRepository cashierDBRepository(JdbcUtils jdbcUtils) {
        return new CashierDBRepository(jdbcUtils);
    }

    @Bean
    public TicketDBRepository ticketDBRepository(JdbcUtils jdbcUtils, GameDBRepository gameDBRepository, CashierDBRepository cashierDBRepository) {
        return new TicketDBRepository(jdbcUtils, gameDBRepository, cashierDBRepository);
    }
}
