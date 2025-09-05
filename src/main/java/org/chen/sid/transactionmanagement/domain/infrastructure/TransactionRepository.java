package org.chen.sid.transactionmanagement.domain.infrastructure;

import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String id);

    List<Transaction> findAll();

    boolean deleteById(String id);

    boolean existsById(String id);
}
