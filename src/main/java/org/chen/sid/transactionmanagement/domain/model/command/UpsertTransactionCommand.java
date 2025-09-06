package org.chen.sid.transactionmanagement.domain.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chen.sid.transactionmanagement.domain.model.entity.TransactionType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertTransactionCommand {
    private String name;

    private BigDecimal amount;

    private String category;

    private TransactionType type;

    public static UpsertTransactionCommand of(String name, BigDecimal amount) {
        return UpsertTransactionCommand.builder().name(name).amount(amount).build();
    }

    public static UpsertTransactionCommand of(String name, BigDecimal amount, String category, TransactionType type) {
        return UpsertTransactionCommand.builder().name(name).amount(amount).category(category).type(type).build();
    }
}
