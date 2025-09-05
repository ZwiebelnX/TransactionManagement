package org.chen.sid.transactionmanagement.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chen.sid.transactionmanagement.adapter.in.dto.CreateTransactionRequestDTO;
import org.chen.sid.transactionmanagement.adapter.in.dto.UpdateTransactionRequestDTO;
import org.chen.sid.transactionmanagement.application.usecase.command.TransactionCommandUseCase;
import org.chen.sid.transactionmanagement.application.usecase.query.TransactionQueryUseCase;
import org.chen.sid.transactionmanagement.common.exception.DataNotFoundException;
import org.chen.sid.transactionmanagement.domain.model.command.CreateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.command.UpdateTransactionCommand;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionCommandUseCase transactionCommandUseCase;

    @MockBean
    private TransactionQueryUseCase transactionQueryUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction sampleTransaction;
    private CreateTransactionRequestDTO createRequest;
    private UpdateTransactionRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        sampleTransaction = Transaction.builder()
                .id("test-id-123")
                .name("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        createRequest = new CreateTransactionRequestDTO();
        createRequest.setName("Test Transaction");
        createRequest.setAmount(new BigDecimal("100.00"));

        updateRequest = new UpdateTransactionRequestDTO();
        updateRequest.setName("Updated Transaction");
        updateRequest.setAmount(new BigDecimal("200.00"));
    }

    @Test
    void should_return_created_transaction_when_valid_request_given() throws Exception {
        when(transactionCommandUseCase.createTransaction(any(CreateTransactionCommand.class))).thenReturn(sampleTransaction);

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.name").value("Test Transaction"))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(transactionCommandUseCase, times(1)).createTransaction(any(CreateTransactionCommand.class));
    }

    @Test
    void should_return_bad_request_when_invalid_request_given() throws Exception {
        createRequest.setName("");
        createRequest.setAmount(null);

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));

        verify(transactionCommandUseCase, never()).createTransaction(any(CreateTransactionCommand.class));
    }

    @Test
    void should_return_bad_request_when_business_exception_thrown() throws Exception {
        when(transactionCommandUseCase.createTransaction(any(CreateTransactionCommand.class))).thenThrow(
                new IllegalArgumentException("Transaction amount cannot be negative"));

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Transaction amount cannot be negative"));
    }

    @Test
    void should_return_updated_transaction_when_valid_update_request_given() throws Exception {
        when(transactionCommandUseCase.updateTransaction(any(UpdateTransactionCommand.class))).thenReturn(sampleTransaction);

        mockMvc.perform(put("/api/v1/transactions/test-id-123").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.name").value("Test Transaction"));

        verify(transactionCommandUseCase, times(1)).updateTransaction(any(UpdateTransactionCommand.class));
    }

    @Test
    void should_return_not_found_when_transaction_does_not_exist() throws Exception {
        when(transactionCommandUseCase.updateTransaction(any(UpdateTransactionCommand.class))).thenThrow(
                new DataNotFoundException("Transaction not found with id: non-existent"));

        mockMvc.perform(put("/api/v1/transactions/non-existent").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Business exception"));
    }

    @Test
    void should_return_transaction_when_valid_id_given() throws Exception {
        when(transactionQueryUseCase.getTransactionById("test-id-123")).thenReturn(Optional.of(sampleTransaction));

        mockMvc.perform(get("/api/v1/transactions/test-id-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.name").value("Test Transaction"));

        verify(transactionQueryUseCase, times(1)).getTransactionById("test-id-123");
    }

    @Test
    void should_return_internal_error_when_transaction_id_does_not_exist() throws Exception {
        when(transactionQueryUseCase.getTransactionById("non-existent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/transactions/non-existent"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"));

        verify(transactionQueryUseCase, times(1)).getTransactionById("non-existent");
    }

    @Test
    void should_return_all_transactions_when_transactions_exist() throws Exception {
        Transaction transaction2 = Transaction.builder()
                .id("test-id-456")
                .name("Another Transaction")
                .amount(new BigDecimal("200.00"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        List<Transaction> transactions = Arrays.asList(sampleTransaction, transaction2);
        when(transactionQueryUseCase.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("test-id-123"))
                .andExpect(jsonPath("$[1].id").value("test-id-456"));

        verify(transactionQueryUseCase, times(1)).getAllTransactions();
    }

    @Test
    void should_return_empty_list_when_no_transactions_exist() throws Exception {
        when(transactionQueryUseCase.getAllTransactions()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(transactionQueryUseCase, times(1)).getAllTransactions();
    }

    @Test
    void should_delete_transaction_when_valid_id_given() throws Exception {
        doNothing().when(transactionCommandUseCase).deleteTransaction(anyString());

        mockMvc.perform(delete("/api/v1/transactions/test-id-123")).andExpect(status().isNoContent());

        verify(transactionCommandUseCase, times(1)).deleteTransaction(anyString());
    }

    @Test
    void should_return_not_found_when_deleting_nonexistent_transaction() throws Exception {
        doThrow(new DataNotFoundException("Transaction not found with id: non-existent")).when(transactionCommandUseCase)
                .deleteTransaction(anyString());

        mockMvc.perform(delete("/api/v1/transactions/non-existent"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Business exception"));

        verify(transactionCommandUseCase, times(1)).deleteTransaction(anyString());
    }
}