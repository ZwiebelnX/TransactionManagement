package org.chen.sid.transactionmanagement.application.usecase.query;

import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionQueryUseCase {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionQueryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Optional<Transaction> getTransactionById(String id) {
        validateId(id);
        return transactionRepository.findById(id);
    }

    public Page<Transaction> getPageTransactions(long page, long limit) {
        return transactionRepository.findPage(page, limit);
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
    }
}
