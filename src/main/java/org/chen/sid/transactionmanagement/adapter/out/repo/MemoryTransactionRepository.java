package org.chen.sid.transactionmanagement.adapter.out.repo;

import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class MemoryTransactionRepository implements TransactionRepository {

    private final ConcurrentHashMap<String, Transaction> transactionStore = new ConcurrentHashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        if (transaction.getId() == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        lock.writeLock().lock();
        try {
            transactionStore.put(transaction.getId(), transaction);
            return transaction;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Transaction> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        lock.readLock().lock();
        try {
            return Optional.ofNullable(transactionStore.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Page<Transaction> findPage(long page, long size) {
        lock.readLock().lock();
        try {
            long total = transactionStore.mappingCount();
            List<Transaction> data = transactionStore.values().stream().skip((page - 1) * size).limit(size).toList();
            return new Page<>(total, data);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        lock.writeLock().lock();
        try {
            return transactionStore.remove(id) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        lock.readLock().lock();
        try {
            return transactionStore.containsKey(id);
        } finally {
            lock.readLock().unlock();
        }
    }
}
