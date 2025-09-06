package org.chen.sid.transactionmanagement.application.usecase.query;

import lombok.RequiredArgsConstructor;
import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.application.usecase.query.dto.TransactionDTO;
import org.chen.sid.transactionmanagement.application.validator.CommonRequestParamValidator;
import org.chen.sid.transactionmanagement.common.exception.DataNotFoundException;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionQueryUseCase {
    private final TransactionRepository transactionRepository;

    @Cacheable(value = "transaction", key = "#id")
    public TransactionDTO getTransactionById(String id) {
        CommonRequestParamValidator.validateId(id);
        return TransactionDTO.from(transactionRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Transaction not found")));
    }

    public Page<TransactionDTO> getPageTransactions(long page, long limit) {
        CommonRequestParamValidator.validatePaginationParameters(page, limit);
        Page<Transaction> transactionPage = transactionRepository.findPage(page, limit);
        return new Page<>(transactionPage.getTotal(), transactionPage.getData().stream().map(TransactionDTO::from).toList());
    }
}
