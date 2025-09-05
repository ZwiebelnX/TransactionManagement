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
public class UpdateTransactionCommand {
    private String id;

    private String name;

    private BigDecimal amount;

    public static UpdateTransactionCommand of(String id, String name, BigDecimal amount) {
        return UpdateTransactionCommand.builder().id(id).name(name).amount(amount).build();
    }
}
