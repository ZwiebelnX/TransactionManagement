package org.chen.sid.transactionmanagement.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chen.sid.transactionmanagement.domain.model.command.UpsertTransactionCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;

    private String name;

    private BigDecimal amount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static Transaction create(UpsertTransactionCommand command) {
        validateName(command.getName());
        validateAmount(command.getAmount());

        LocalDateTime now = LocalDateTime.now();
        return Transaction.builder()
                .id(UUID.randomUUID().toString())
                .name(command.getName().trim())
                .amount(command.getAmount())
                .createTime(now)
                .updateTime(now)
                .build();
    }

    public void update(UpsertTransactionCommand command) {
        if (command.getName() != null) {
            validateName(command.getName());
            this.name = command.getName().trim();
        }
        if (command.getAmount() != null) {
            validateAmount(command.getAmount());
            this.amount = command.getAmount();
        }
        this.updateTime = LocalDateTime.now();
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Transaction name cannot exceed 100 characters");
        }
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Transaction amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative");
        }
        if (amount.scale() > 2) {
            throw new IllegalArgumentException("Transaction amount cannot have more than 2 decimal places");
        }
    }
}
