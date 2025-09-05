package org.chen.sid.transactionmanagement.domain.model.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionCommand {
    private String name;

    private BigDecimal amount;

    public static CreateTransactionCommand of(String name, BigDecimal amount) {
        return CreateTransactionCommand.builder().name(name).amount(amount).build();
    }
}
