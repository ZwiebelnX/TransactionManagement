package org.chen.sid.transactionmanagement.application.usecase;

import org.chen.sid.transactionmanagement.application.usecase.query.TransactionQueryUseCase;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionQueryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionQueryUseCase transactionQueryUseCase;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        sampleTransaction = Transaction.builder()
                .id("test-id-123")
                .name("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void should_return_transaction_when_valid_id_given() {
        when(transactionRepository.findById("test-id-123")).thenReturn(Optional.of(sampleTransaction));

        Optional<Transaction> result = transactionQueryUseCase.getTransactionById("test-id-123");

        assertTrue(result.isPresent());
        assertEquals("test-id-123", result.get().getId());
        assertEquals("Test Transaction", result.get().getName());
        verify(transactionRepository, times(1)).findById("test-id-123");
    }

    @Test
    void should_return_empty_when_transaction_not_found() {
        when(transactionRepository.findById("non-existent")).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionQueryUseCase.getTransactionById("non-existent");

        assertFalse(result.isPresent());
        verify(transactionRepository, times(1)).findById("non-existent");
    }

    @Test
    void should_throw_exception_when_null_id_given() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionQueryUseCase.getTransactionById(null));

        assertEquals("Transaction ID cannot be null or empty", exception.getMessage());
        verify(transactionRepository, never()).findById(any());
    }

    @Test
    void should_throw_exception_when_empty_id_given() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionQueryUseCase.getTransactionById(""));

        assertEquals("Transaction ID cannot be null or empty", exception.getMessage());
        verify(transactionRepository, never()).findById(any());
    }

    @Test
    void should_return_all_transactions_when_transactions_exist() {
        Transaction transaction2 = Transaction.builder()
                .id("test-id-456")
                .name("Another Transaction")
                .amount(new BigDecimal("200.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        List<Transaction> transactions = Arrays.asList(sampleTransaction, transaction2);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionQueryUseCase.getAllTransactions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(sampleTransaction));
        assertTrue(result.contains(transaction2));
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void should_return_empty_list_when_no_transactions_exist() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        List<Transaction> result = transactionQueryUseCase.getAllTransactions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).findAll();
    }
}
