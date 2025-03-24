package org.example.service;

import org.example.model.Cashier;
import org.example.repository.CashierRepository;

import java.util.Optional;

public class CashierService {
    private final CashierRepository cashierRepository;

    public CashierService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    public Iterable<Cashier> getAllCashiers() {
        return cashierRepository.findAll();
    }

    public Optional<Cashier> getCashierById(Integer id) {
        return cashierRepository.findById(id);
    }

    public Cashier saveCashier(Cashier cashier) {
        return cashierRepository.save(cashier);
    }

    public Optional<Cashier> deleteCashierById(Integer id) {
        return cashierRepository.deleteById(id);
    }

    public Cashier updateCashier(Cashier cashier) {
        return cashierRepository.update(cashier);
    }

    public Optional<Cashier> getCashierByUsername(String username) {
        return cashierRepository.findByUsername(username);
    }
}
