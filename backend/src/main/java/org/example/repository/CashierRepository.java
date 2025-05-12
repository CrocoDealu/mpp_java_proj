package org.example.repository;

import org.example.model.Cashier;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashierRepository extends IRepository<Integer, Cashier> {
    public Optional<Cashier> findByUsername(String username);
}
