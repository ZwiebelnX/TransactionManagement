package org.chen.sid.transactionmanagement.application.usecase.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chen.sid.transactionmanagement.domain.model.entity.TransactionType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create transaction request")
public class UpsertTransactionRequestDTO {

    public UpsertTransactionRequestDTO(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }

    @NotBlank(message = "Transaction name cannot be null or empty")
    @Size(max = 100, message = "Transaction name cannot exceed 100 characters")
    @Schema(description = "Transaction name", example = "Purchase goods")
    private String name;

    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Transaction amount cannot be negative")
    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;

    @Schema(description = "Transaction category", example = "Food")
    private String category;

    @Schema(description = "Transaction type", example = "DEPOSIT")
    private TransactionType type;
}