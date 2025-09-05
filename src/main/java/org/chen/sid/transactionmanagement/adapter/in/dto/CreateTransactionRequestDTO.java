package org.chen.sid.transactionmanagement.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Create transaction request")
public class CreateTransactionRequestDTO {

    @NotBlank(message = "Transaction name cannot be null or empty")
    @Size(max = 100, message = "Transaction name cannot exceed 100 characters")
    @Schema(description = "Transaction name", example = "Purchase goods")
    private String name;

    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Transaction amount cannot be negative")
    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal amount;
}