package org.example.repository;

import org.example.model.Game;
import org.example.model.Ticket;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class HibernateGameRepository implements GameRepository {

    private final SessionFactory sessionFactory;

    public HibernateGameRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Iterable<Game> findGamesForTickets(Iterable<Ticket> tickets) {
        return null;
    }

    @Override
    public Iterable<Game> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Game", Game.class).list();
        }
    }

    @Override
    public Optional<Game> findById(Integer integer) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Game.class, integer));
        }
    }

    @Override
    public Game save(Game entity) {
        return null;
    }

    @Override
    public Optional<Game> deleteById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Game update(Game entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Game updatedGame = session.merge(entity);
            transaction.commit();
            return updatedGame;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Error updating game", e);
        }
    }
}
