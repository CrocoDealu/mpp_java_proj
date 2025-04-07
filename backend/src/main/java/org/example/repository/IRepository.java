package org.example.repository;

import org.example.model.Entity;

import java.util.Optional;

public interface IRepository<ID, E extends Entity<ID>> {

    /**
     * Retrieve all records from the repository.
     * @return a list of all records.
     */
    Iterable<E> findAll();

    /**
     * Retrieve a record by its unique identifier.
     * @param id the identifier of the record.
     * @return an Optional containing the record, or an empty Optional if not found.
     */
    Optional<E> findById(ID id);

    /**
     * Save a record to the repository.
     * @param entity the entity to be saved.
     * @return the saved entity.
     */
    E save(E entity);

    /**
     * Delete a record by its unique identifier.
     * @param id the identifier of the record to delete.
     */
    Optional<E> deleteById(ID id);

    /**
     * Update an existing record in the repository.
     * @param entity the entity to update.
     * @return the updated entity.
     */
    E update(E entity);
}
