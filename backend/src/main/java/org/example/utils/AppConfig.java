package org.example.utils;

import org.example.network.ClientManager;
import org.example.network.JSONServer;
import org.example.network.RequestHandler;
import org.example.repository.*;
import org.example.service.SportsTicketManagementService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileReader;
import java.util.Objects;
import java.util.Properties;

@org.springframework.context.annotation.Configuration
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
    public SessionFactory sessionFactory() {
        try {
            Configuration hibernateConfig = new Configuration();

            hibernateConfig.configure("hibernate.cfg.xml");

            return hibernateConfig.buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure Hibernate SessionFactory", e);
        }
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
//    public GameRepository gameRepository(JdbcUtils jdbcUtils) {
//        return new GameDBRepository(jdbcUtils);
//    }
    public GameRepository gameRepository(SessionFactory sessionFactory) {
        return new HibernateGameRepository(sessionFactory);
    }

    @Bean
    public SportsTicketManagementService sportsTicketManagementService(GameRepository gameRepository, TicketRepository ticketRepository, CashierRepository cashierRepository, ClientManager clientManager) {
        return new SportsTicketManagementService(cashierRepository, gameRepository, ticketRepository, clientManager);
    }

    @Bean
//    public CashierRepository cashierDBRepository(JdbcUtils jdbcUtils) {
//        return new CashierDBRepository(jdbcUtils);
//    }
    public CashierRepository cashierRepository(SessionFactory sessionFactory) {
        return new HibernateCashierRepository(sessionFactory);
    }

    @Bean
    public TicketRepository ticketRepository(JdbcUtils jdbcUtils, GameRepository gameRepository, CashierRepository cashierRepository) {
        return new TicketDBRepository(jdbcUtils, gameRepository, cashierRepository);
    }
}
