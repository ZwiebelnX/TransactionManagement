package org.chen.sid.transactionmanagement.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Update transaction request")
public class UpdateTransactionRequestDTO {

    @Size(max = 100, message = "Transaction name cannot exceed 100 characters")
    @Schema(description = "Transaction name", example = "Updated transaction name")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "Transaction amount cannot be negative")
    @Schema(description = "Transaction amount", example = "200.00")
    private BigDecimal amount;
}