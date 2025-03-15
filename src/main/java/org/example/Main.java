package org.example;

import org.example.dto.ClientFilterDTO;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;
import org.example.repository.CashierDBRepository;
import org.example.repository.GameDBRepository;
import org.example.repository.TicketDBRepository;
import org.example.utils.JdbcUtils;

import javax.swing.text.html.Option;
import java.io.FileReader;
import java.util.Optional;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JdbcUtils jdbcUtils = new JdbcUtils(properties);
        GameDBRepository gameDBRepository = new GameDBRepository(jdbcUtils);
        CashierDBRepository cashierDBRepository = new CashierDBRepository(jdbcUtils);
        TicketDBRepository ticketDBRepository = new TicketDBRepository(jdbcUtils, gameDBRepository, cashierDBRepository);

        Optional<Game> game = gameDBRepository.findById(2);

        Optional<Cashier> cashier = cashierDBRepository.findById(1);

        if (game.isPresent() && cashier.isPresent()) {
//            ticketDBRepository.save(new Ticket(0, game.get(), "David", "Margaretelor", cashier.get(), 2, 10.25F));
//            Iterable<Ticket> it = ticketDBRepository.getTicketsSoldForGame(game.get());
//            it.forEach(System.out::println);
            ticketDBRepository.getTicketsForClient(new ClientFilterDTO("", "Margaretelor")).forEach(System.out::println);
        }
    }
}