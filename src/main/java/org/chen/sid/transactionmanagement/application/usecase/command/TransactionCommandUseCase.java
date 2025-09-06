package org.chen.sid.transactionmanagement.application.usecase.command;

import org.chen.sid.transactionmanagement.application.usecase.command.dto.UpsertTransactionRequestDTO;
import org.chen.sid.transactionmanagement.common.exception.DataNotFoundException;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.command.UpsertTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class TransactionCommandUseCase {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionCommandUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(UpsertTransactionRequestDTO request) {
        UpsertTransactionCommand command = UpsertTransactionCommand.of(request.getName(), request.getAmount());
        Transaction transaction = Transaction.create(command);
        return transactionRepository.save(transaction);
    }

    @CacheEvict(value = "transaction", key = "#id")
    public Transaction updateTransaction(String id, UpsertTransactionRequestDTO request) {
        validateId(id);

        UpsertTransactionCommand command = UpsertTransactionCommand.of(request.getName(), request.getAmount());
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Transaction not found with id: " + id));

        transaction.update(command);
        return transactionRepository.save(transaction);
    }

    @CacheEvict(value = "transaction", key = "#id")
    public void deleteTransaction(String id) {
        validateId(id);
        transactionRepository.deleteById(id);
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
    }
}
