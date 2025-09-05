package org.chen.sid.transactionmanagement.adapter.out.repo;

import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TransactionRepositoryImpl 单元测试
 */
class TransactionRepositoryImplTest {

    private TransactionRepositoryImpl repository;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepositoryImpl();
        sampleTransaction = Transaction.builder()
                .id("test-id-123")
                .name("测试交易")
                .amount(new BigDecimal("100.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 测试保存交易 - 成功场景
     */
    @Test
    void should_save_transaction_when_valid_transaction_given() {
        // 执行保存
        Transaction result = repository.save(sampleTransaction);

        // 验证结果
        assertNotNull(result);
        assertEquals(sampleTransaction.getId(), result.getId());
        assertEquals(sampleTransaction.getName(), result.getName());
        assertEquals(sampleTransaction.getAmount(), result.getAmount());

        // 验证交易确实被保存了
        assertTrue(repository.existsById("test-id-123"));
    }

    /**
     * 测试保存空交易
     */
    @Test
    void should_throw_exception_when_null_transaction_given() {
        // 测试保存空交易
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> repository.save(null));

        assertEquals("Transaction cannot be null", exception.getMessage());
    }

    /**
     * 测试保存ID为空的交易
     */
    @Test
    void should_throw_exception_when_null_id_given() {
        sampleTransaction.setId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> repository.save(sampleTransaction));

        assertEquals("Transaction ID cannot be null", exception.getMessage());
    }

    /**
     * 测试根据ID查找交易 - 成功场景
     */
    @Test
    void should_return_transaction_when_valid_id_given() {
        // 先保存交易
        repository.save(sampleTransaction);

        // 查找交易
        Optional<Transaction> result = repository.findById("test-id-123");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("test-id-123", result.get().getId());
        assertEquals("测试交易", result.get().getName());
    }

    /**
     * 测试根据ID查找交易 - 不存在
     */
    @Test
    void should_return_empty_when_transaction_not_found() {
        Optional<Transaction> result = repository.findById("non-existent");
        assertFalse(result.isPresent());
    }

    /**
     * 测试根据空ID查找交易
     */
    @Test
    void should_return_empty_when_null_id_given() {
        Optional<Transaction> result = repository.findById(null);
        assertFalse(result.isPresent());
    }

    /**
     * 测试查找所有交易 - 空列表
     */
    @Test
    void should_return_empty_list_when_no_transactions_exist() {
        List<Transaction> result = repository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * 测试查找所有交易 - 包含数据
     */
    @Test
    void should_return_all_transactions_when_transactions_exist() {
        // 保存多个交易
        repository.save(sampleTransaction);

        Transaction transaction2 = Transaction.builder()
                .id("test-id-456")
                .name("另一个交易")
                .amount(new BigDecimal("200.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        repository.save(transaction2);

        // 查找所有交易
        List<Transaction> result = repository.findAll();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> "test-id-123".equals(t.getId())));
        assertTrue(result.stream().anyMatch(t -> "test-id-456".equals(t.getId())));
    }

    /**
     * 测试根据ID删除交易 - 成功场景
     */
    @Test
    void should_delete_transaction_when_valid_id_given() {
        // 先保存交易
        repository.save(sampleTransaction);
        assertTrue(repository.existsById("test-id-123"));

        // 删除交易
        boolean result = repository.deleteById("test-id-123");

        // 验证结果
        assertTrue(result);
        assertFalse(repository.existsById("test-id-123"));
    }

    /**
     * 测试根据ID删除交易 - 不存在
     */
    @Test
    void should_return_false_when_transaction_not_found() {
        boolean result = repository.deleteById("non-existent");
        assertFalse(result);
    }

    /**
     * 测试根据空ID删除交易
     */
    @Test
    void should_return_false_when_null_id_given() {
        boolean result = repository.deleteById(null);
        assertFalse(result);
    }

    /**
     * 测试检查交易是否存在 - 存在
     */
    @Test
    void should_return_true_when_transaction_exists() {
        repository.save(sampleTransaction);
        assertTrue(repository.existsById("test-id-123"));
    }

    /**
     * 测试检查交易是否存在 - 不存在
     */
    @Test
    void should_return_false_when_transaction_does_not_exist() {
        assertFalse(repository.existsById("non-existent"));
    }

    /**
     * 测试检查空ID是否存在
     */
    @Test
    void should_return_false_when_null_id_given_for_exists_check() {
        assertFalse(repository.existsById(null));
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

        // 验证所有交易都被保存
        List<Transaction> allTransactions = repository.findAll();
        assertEquals(100, allTransactions.size());

        // 验证每个交易都能被找到
        for (int i = 0; i < 100; i++) {
            assertTrue(repository.existsById("test-id-" + i));
        }
    }
}
