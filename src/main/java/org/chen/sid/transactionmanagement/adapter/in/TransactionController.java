package org.chen.sid.transactionmanagement.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.chen.sid.transactionmanagement.adapter.in.dto.CreateTransactionRequestDTO;
import org.chen.sid.transactionmanagement.adapter.in.dto.UpdateTransactionRequestDTO;
import org.chen.sid.transactionmanagement.application.usecase.command.TransactionCommandUseCase;
import org.chen.sid.transactionmanagement.application.usecase.query.TransactionQueryUseCase;
import org.chen.sid.transactionmanagement.domain.model.command.CreateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.command.UpdateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Transaction Management", description = "Transaction CRUD operations using CQRS pattern")
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionCommandUseCase transactionCommandUseCase;

    private final TransactionQueryUseCase transactionQueryUseCase;

    @Autowired
    public TransactionController(TransactionCommandUseCase transactionCommandUseCase, TransactionQueryUseCase transactionQueryUseCase) {
        this.transactionCommandUseCase = transactionCommandUseCase;
        this.transactionQueryUseCase = transactionQueryUseCase;
    }

    @Operation(summary = "Create transaction", description = "Create a new transaction using Command pattern")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Transaction created successfully"),
                           @ApiResponse(responseCode = "400", description = "Invalid request data")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction createTransaction(@Valid @RequestBody CreateTransactionRequestDTO request) {
        CreateTransactionCommand command = CreateTransactionCommand.of(request.getName(), request.getAmount());
        return transactionCommandUseCase.createTransaction(command);
    }

    @Operation(summary = "Update transaction", description = "Update an existing transaction using Command pattern")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
                           @ApiResponse(responseCode = "400", description = "Invalid request data"),
                           @ApiResponse(responseCode = "404", description = "Transaction not found")})
    @PutMapping("/{id}")
    public Transaction updateTransaction(@Parameter(description = "Transaction ID") @PathVariable String id,
            @Valid @RequestBody UpdateTransactionRequestDTO request) {
        UpdateTransactionCommand command = UpdateTransactionCommand.of(id, request.getName(), request.getAmount());
        return transactionCommandUseCase.updateTransaction(command);
    }

    @Operation(summary = "Get transaction", description = "Get transaction by ID using Query pattern")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Transaction found"),
                           @ApiResponse(responseCode = "400", description = "Invalid transaction ID"),
                           @ApiResponse(responseCode = "404", description = "Transaction not found")})
    @GetMapping("/{id}")
    public Transaction getTransactionById(@Parameter(description = "Transaction ID") @PathVariable String id) {
        Optional<Transaction> transaction = transactionQueryUseCase.getTransactionById(id);
        if (transaction.isEmpty()) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        return transaction.get();
    }

    @Operation(summary = "List transactions", description = "Get all transactions using Query pattern")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")})
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionQueryUseCase.getAllTransactions();
    }

    @Operation(summary = "Delete transaction", description = "Delete transaction by ID using Command pattern")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
                           @ApiResponse(responseCode = "400", description = "Invalid transaction ID"),
                           @ApiResponse(responseCode = "404", description = "Transaction not found")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@Parameter(description = "Transaction ID") @PathVariable String id) {
        transactionCommandUseCase.deleteTransaction(id);
    }
}