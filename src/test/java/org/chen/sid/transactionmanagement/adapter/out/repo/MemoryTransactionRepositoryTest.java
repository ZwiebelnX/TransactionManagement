package org.chen.sid.transactionmanagement.adapter.out.repo;

import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryTransactionRepositoryTest {

    private MemoryTransactionRepository repository;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        repository = new MemoryTransactionRepository();
        sampleTransaction = Transaction.builder()
                .id("test-id-123")
                .name("测试交易")
                .amount(new BigDecimal("100.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void should_save_transaction_when_valid_transaction_given() {
        Transaction result = repository.save(sampleTransaction);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sampleTransaction.getId());
        assertThat(result.getName()).isEqualTo(sampleTransaction.getName());
        assertThat(result.getAmount()).isEqualTo(sampleTransaction.getAmount());

        assertThat(repository.existsById("test-id-123")).isTrue();
    }

    @Test
    void should_throw_exception_when_null_transaction_given() {
        assertThatThrownBy(() -> repository.save(null)).isInstanceOf(IllegalArgumentException.class).hasMessage("Transaction cannot be null");
    }

    @Test
    void should_throw_exception_when_null_id_given() {
        sampleTransaction.setId(null);

        assertThatThrownBy(() -> repository.save(sampleTransaction)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction ID cannot be null");
    }

    @Test
    void should_return_transaction_when_valid_id_given() {
        repository.save(sampleTransaction);

        Optional<Transaction> result = repository.findById("test-id-123");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("test-id-123");
        assertThat(result.get().getName()).isEqualTo("测试交易");
    }

    @Test
    void should_return_empty_when_transaction_not_found() {
        Optional<Transaction> result = repository.findById("non-existent");
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_empty_when_null_id_given() {
        Optional<Transaction> result = repository.findById(null);
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_empty_list_when_no_transactions_exist() {
        Page<Transaction> result = repository.findPage(1, 10);

        assertThat(result).isNotNull();
        assertThat(result.getData()).isEmpty();
    }

    /**
     * 测试查找所有交易 - 包含数据
     */
    @Test
    void should_return_all_transactions_when_transactions_exist() {
        repository.save(sampleTransaction);

        Transaction transaction2 = Transaction.builder()
                .id("test-id-456")
                .name("另一个交易")
                .amount(new BigDecimal("200.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        repository.save(transaction2);

        Page<Transaction> result = repository.findPage(1, 10);

        assertThat(result).isNotNull();
        assertThat(result.getData()).hasSize(2);
        assertThat(result.getData()).anyMatch(t -> "test-id-123".equals(t.getId()));
        assertThat(result.getData()).anyMatch(t -> "test-id-456".equals(t.getId()));
    }

    @Test
    void should_delete_transaction_when_valid_id_given() {
        repository.save(sampleTransaction);
        assertThat(repository.existsById("test-id-123")).isTrue();

        boolean result = repository.deleteById("test-id-123");

        assertThat(result).isTrue();
        assertThat(repository.existsById("test-id-123")).isFalse();
    }

    @Test
    void should_return_false_when_transaction_not_found() {
        boolean result = repository.deleteById("non-existent");
        assertThat(result).isFalse();
    }

    @Test
    void should_return_false_when_null_id_given() {
        boolean result = repository.deleteById(null);
        assertThat(result).isFalse();
    }

    @Test
    void should_return_true_when_transaction_exists() {
        repository.save(sampleTransaction);
        assertThat(repository.existsById("test-id-123")).isTrue();
    }

    @Test
    void should_return_false_when_transaction_does_not_exist() {
        assertThat(repository.existsById("non-existent")).isFalse();
    }

    /**
     * 测试检查空ID是否存在
     */
    @Test
    void should_return_false_when_null_id_given_for_exists_check() {
        assertThat(repository.existsById(null)).isFalse();
    }

    /**
     * 测试并发安全性 - 基本测试
     */
    @Test
    void should_handle_concurrent_operations_when_multiple_transactions_given() {
        // 创建多个交易
        for (int i = 0; i < 100; i++) {
            Transaction transaction = Transaction.builder()
                    .id("test-id-" + i)
                    .name("交易 " + i)
                    .amount(new BigDecimal(i))
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            repository.save(transaction);
        }

        Page<Transaction> allTransactions = repository.findPage(2, 10);
        assertThat(allTransactions.getData()).hasSize(10);
        assertThat(allTransactions.getTotal()).isEqualTo(100);

        for (int i = 11; i < 21; i++) {
            assertThat(repository.existsById("test-id-" + i)).isTrue();
        }
    }
}
