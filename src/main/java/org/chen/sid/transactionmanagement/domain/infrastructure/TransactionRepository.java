package org.chen.sid.transactionmanagement.domain.infrastructure;

import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;

import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String id);

    Page<Transaction> findPage(long page, long size);

    boolean deleteById(String id);

    boolean existsById(String id);
}
