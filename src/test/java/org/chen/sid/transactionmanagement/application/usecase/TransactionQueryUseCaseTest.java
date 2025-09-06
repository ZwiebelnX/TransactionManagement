package org.chen.sid.transactionmanagement.application.usecase;

import org.chen.sid.transactionmanagement.application.usecase.query.TransactionQueryUseCase;
import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.common.exception.DataNotFoundException;
import org.chen.sid.transactionmanagement.common.exception.RequestArgumentIllegalException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
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

        Transaction result = transactionQueryUseCase.getTransactionById("test-id-123");

        assertThat(result.getId()).isEqualTo("test-id-123");
        assertThat(result.getName()).isEqualTo("Test Transaction");
        verify(transactionRepository, times(1)).findById("test-id-123");
    }

    @Test
    void should_return_empty_when_transaction_not_found() {
        when(transactionRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionQueryUseCase.getTransactionById("non-existent")).isInstanceOf(DataNotFoundException.class);
        verify(transactionRepository, times(1)).findById("non-existent");
    }

    @Test
    void should_throw_exception_when_null_id_given() {
        assertThatThrownBy(() -> transactionQueryUseCase.getTransactionById(null)).isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Transaction ID cannot be null or empty");
        verify(transactionRepository, never()).findById(any());
    }

    @Test
    void should_throw_exception_when_empty_id_given() {
        assertThatThrownBy(() -> transactionQueryUseCase.getTransactionById("")).isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Transaction ID cannot be null or empty");
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
        when(transactionRepository.findPage(1, 10)).thenReturn(new Page<>(2, transactions));

        Page<Transaction> result = transactionQueryUseCase.getPageTransactions(1, 10);

        assertThat(result).isNotNull();
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData()).contains(sampleTransaction, transaction2);
        verify(transactionRepository, times(1)).findPage(1, 10);
    }

    @Test
    void should_return_empty_list_when_no_transactions_exist() {
        when(transactionRepository.findPage(1, 10)).thenReturn(new Page<>(0, List.of()));

        Page<Transaction> result = transactionQueryUseCase.getPageTransactions(1, 10);

        assertThat(result).isNotNull();
        assertThat(result.getData()).isEmpty();
        verify(transactionRepository, times(1)).findPage(1, 10);
    }

    @Test
    void should_throw_exception_when_page_is_zero() {
        assertThatThrownBy(() -> transactionQueryUseCase.getPageTransactions(0, 10)).isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page number must be greater than 0");
        verify(transactionRepository, never()).findPage(anyLong(), anyLong());
    }

    @Test
    void should_throw_exception_when_page_is_negative() {
        assertThatThrownBy(() -> transactionQueryUseCase.getPageTransactions(-1, 10)).isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page number must be greater than 0");
        verify(transactionRepository, never()).findPage(anyLong(), anyLong());
    }

    @Test
    void should_throw_exception_when_size_is_zero() {
        assertThatThrownBy(() -> transactionQueryUseCase.getPageTransactions(1, 0)).isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page size must be greater than 0");
        verify(transactionRepository, never()).findPage(anyLong(), anyLong());
    }

    @Test
    void should_throw_exception_when_size_is_negative() {
        assertThatThrownBy(() -> transactionQueryUseCase.getPageTransactions(1, -1)).isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page size must be greater than 0");
        verify(transactionRepository, never()).findPage(anyLong(), anyLong());
    }

    @Test
    void should_throw_exception_when_both_page_and_size_are_invalid() {
        assertThatThrownBy(() -> transactionQueryUseCase.getPageTransactions(0, 0)).isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page number must be greater than 0");
        verify(transactionRepository, never()).findPage(anyLong(), anyLong());
    }
}
