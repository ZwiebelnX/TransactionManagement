package org.chen.sid.transactionmanagement.adapter.out.repo;

import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemoryTransactionRepository implements TransactionRepository {

    private final ConcurrentHashMap<String, Transaction> transactionStore = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.getId() == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        transactionStore.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(transactionStore.get(id));
    }

    @Override
    public Page<Transaction> findPage(long page, long size) {
        long total = transactionStore.mappingCount();
        List<Transaction> data = transactionStore.values().stream().skip((page - 1) * size).limit(size).toList();
        return new Page<>(total, data);
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        return transactionStore.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return transactionStore.containsKey(id);
    }
}
