package org.chen.sid.transactionmanagement.application.usecase;

import org.chen.sid.transactionmanagement.application.usecase.command.TransactionCommandUseCase;
import org.chen.sid.transactionmanagement.common.exception.DataNotFoundException;
import org.chen.sid.transactionmanagement.domain.infrastructure.TransactionRepository;
import org.chen.sid.transactionmanagement.domain.model.command.CreateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.command.UpdateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionCommandUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionCommandUseCase transactionCommandUseCase;

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
    void should_create_transaction_when_valid_command_given() {
        CreateTransactionCommand command = CreateTransactionCommand.of("Test Transaction", new BigDecimal("100.00"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);

        Transaction result = transactionCommandUseCase.createTransaction(command);

        assertNotNull(result);
        assertEquals("Test Transaction", result.getName());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void should_throw_exception_when_null_name_given() {
        CreateTransactionCommand command = CreateTransactionCommand.of(null, new BigDecimal("100.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionCommandUseCase.createTransaction(command));

        assertEquals("Transaction name cannot be null or empty", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_negative_amount_given() {
        CreateTransactionCommand command = CreateTransactionCommand.of("Test Transaction", new BigDecimal("-100.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionCommandUseCase.createTransaction(command));

        assertEquals("Transaction amount cannot be negative", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void should_update_transaction_when_valid_command_given() {
        UpdateTransactionCommand command = UpdateTransactionCommand.of("test-id-123", "Updated Transaction", new BigDecimal("200.00"));
        when(transactionRepository.findById("test-id-123")).thenReturn(Optional.of(sampleTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleTransaction);

        Transaction result = transactionCommandUseCase.updateTransaction(command);

        assertNotNull(result);
        verify(transactionRepository, times(1)).findById("test-id-123");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void should_throw_exception_when_transaction_not_found() {
        UpdateTransactionCommand command = UpdateTransactionCommand.of("non-existent", "Updated Transaction", new BigDecimal("200.00"));
        when(transactionRepository.findById("non-existent")).thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> transactionCommandUseCase.updateTransaction(command));

        assertTrue(exception.getMessage().contains("Transaction not found with id: non-existent"));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_null_id_given_for_update() {
        UpdateTransactionCommand command = UpdateTransactionCommand.of(null, "Updated Transaction", new BigDecimal("200.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionCommandUseCase.updateTransaction(command));

        assertEquals("Transaction ID cannot be null or empty", exception.getMessage());
        verify(transactionRepository, never()).findById(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void should_throw_exception_when_empty_id_given_for_update() {
        UpdateTransactionCommand command = UpdateTransactionCommand.of("", "Updated Transaction", new BigDecimal("200.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionCommandUseCase.updateTransaction(command));

        assertEquals("Transaction ID cannot be null or empty", exception.getMessage());
        verify(transactionRepository, never()).findById(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void should_delete_transaction_when_valid_id_given() {
        String id = "test-id-123";

        transactionCommandUseCase.deleteTransaction(id);

        verify(transactionRepository, times(1)).deleteById(id);
    }

    @Test
    void should_call_delete_when_transaction_id_given() {
        String id = "non-existent";

        transactionCommandUseCase.deleteTransaction(id);

        verify(transactionRepository, times(1)).deleteById(id);
    }

    @Test
    void should_throw_exception_when_null_id_given_for_deletion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionCommandUseCase.deleteTransaction(null));

        assertEquals("Transaction ID cannot be null or empty", exception.getMessage());
        verify(transactionRepository, never()).existsById(any());
        verify(transactionRepository, never()).deleteById(any());
    }

    @Test
    void should_throw_exception_when_empty_id_given_for_deletion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> transactionCommandUseCase.deleteTransaction(""));

        assertEquals("Transaction ID cannot be null or empty", exception.getMessage());
        verify(transactionRepository, never()).existsById(any());
        verify(transactionRepository, never()).deleteById(any());
    }
}