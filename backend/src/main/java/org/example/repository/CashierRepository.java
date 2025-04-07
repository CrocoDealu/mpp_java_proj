package org.example.repository;

import org.example.model.Cashier;

import java.util.Optional;

public interface CashierRepository extends IRepository<Integer, Cashier> {
    public Optional<Cashier> findByUsername(String username);
}
