package org.chen.sid.transactionmanagement.application.usecase.command;

import org.chen.sid.transactionmanagement.common.exception.DataNotFoundException;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.command.CreateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.command.UpdateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionCommandUseCase {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionCommandUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(CreateTransactionCommand command) {
        Transaction transaction = Transaction.create(command);
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(UpdateTransactionCommand command) {
        validateId(command.getId());

        Transaction transaction = transactionRepository.findById(command.getId())
                .orElseThrow(() -> new DataNotFoundException("Transaction not found with id: " + command.getId()));

        transaction.update(command);
        return transactionRepository.save(transaction);
    }

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
