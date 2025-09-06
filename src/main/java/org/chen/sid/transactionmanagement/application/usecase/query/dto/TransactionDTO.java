package org.chen.sid.transactionmanagement.application.usecase.query.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.chen.sid.transactionmanagement.domain.model.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    @Schema(description = "Transaction id")
    private String id;

    @Schema(description = "Transaction name", example = "Purchase goods")
    private String name;

    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;

    @Schema(description = "Transaction category", example = "Daily")
    private String category;

    @Schema(description = "Transaction type", example = "DEPOSIT")
    private TransactionType type;

    @Schema(description = "Transaction create time")
    private LocalDateTime createTime;

    @Schema(description = "Transaction update time")
    private LocalDateTime updateTime;

    public static TransactionDTO from(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setName(transaction.getName());
        dto.setAmount(transaction.getAmount());
        dto.setCategory(transaction.getCategory());
        dto.setType(transaction.getType());
        dto.setCreateTime(transaction.getCreateTime());
        dto.setUpdateTime(transaction.getUpdateTime());
        return dto;
    }
}
