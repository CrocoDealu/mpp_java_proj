package org.example.repository;

import org.hibernate.query.Query;
import org.example.model.Cashier;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.util.Optional;

public class HibernateCashierRepository implements CashierRepository {

    private final SessionFactory sessionFactory;

    public HibernateCashierRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Cashier> findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Cashier WHERE username = :username";
            Query<Cashier> query = session.createQuery(hql, Cashier.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        }
    }

    @Override
    public Iterable<Cashier> findAll() {
        return null;
    }

    @Override
    public Optional<Cashier> findById(Integer integer) {

        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Cashier.class, integer));
        }
    }

    @Override
    public Cashier save(Cashier entity) {
        return null;
    }

    @Override
    public Optional<Cashier> deleteById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Cashier update(Cashier entity) {
        return null;
    }
}
