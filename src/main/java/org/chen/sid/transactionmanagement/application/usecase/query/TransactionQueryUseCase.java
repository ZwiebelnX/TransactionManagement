package org.chen.sid.transactionmanagement.application.usecase.query;

import lombok.RequiredArgsConstructor;
import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.common.exception.RequestArgumentIllegalException;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionQueryUseCase {
    private final TransactionRepository transactionRepository;

    private static final int MAX_PAGE_SIZE = 1000;

    public Optional<Transaction> getTransactionById(String id) {
        validateId(id);
        return transactionRepository.findById(id);
    }

    public Page<Transaction> getPageTransactions(long page, long limit) {
        validatePaginationParameters(page, limit);
        return transactionRepository.findPage(page, limit);
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RequestArgumentIllegalException("Transaction ID cannot be null or empty");
        }
    }

    private void validatePaginationParameters(long page, long size) {
        if (page <= 0) {
            throw new RequestArgumentIllegalException("Page number must be greater than 0");
        }
        if (size <= 0) {
            throw new RequestArgumentIllegalException("Page size must be greater than 0");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new RequestArgumentIllegalException("Page size must be less than 1000");
        }
    }
}
