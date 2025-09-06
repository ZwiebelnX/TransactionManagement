package org.chen.sid.transactionmanagement.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chen.sid.transactionmanagement.application.usecase.command.TransactionCommandUseCase;
import org.chen.sid.transactionmanagement.application.usecase.command.dto.UpsertTransactionRequestDTO;
import org.chen.sid.transactionmanagement.application.usecase.query.TransactionQueryUseCase;
import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.common.exception.DataNotFoundException;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.chen.sid.transactionmanagement.domain.model.entity.TransactionType;
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

    private UpsertTransactionRequestDTO upsertRequest;

    @BeforeEach
    void setUp() {
        sampleTransaction = Transaction.builder()
                .id("test-id-123")
                .name("Test Transaction")
                .amount(new BigDecimal("100.00")).category("Food").type(TransactionType.DEPOSIT)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        upsertRequest = new UpsertTransactionRequestDTO();
        upsertRequest.setName("Test Transaction");
        upsertRequest.setAmount(new BigDecimal("100.00"));
        upsertRequest.setCategory("Food");
        upsertRequest.setType(TransactionType.DEPOSIT);
    }

    @Test
    void should_return_created_transaction_when_valid_request_given() throws Exception {
        when(transactionCommandUseCase.createTransaction(any(UpsertTransactionRequestDTO.class))).thenReturn(sampleTransaction);

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(upsertRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.name").value("Test Transaction"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.type").value("DEPOSIT"));

        verify(transactionCommandUseCase, times(1)).createTransaction(any(UpsertTransactionRequestDTO.class));
    }

    @Test
    void should_return_bad_request_when_invalid_request_given() throws Exception {
        upsertRequest.setName("");
        upsertRequest.setAmount(null);

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(upsertRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));

        verify(transactionCommandUseCase, never()).createTransaction(any(UpsertTransactionRequestDTO.class));
    }

    @Test
    void should_return_bad_request_when_business_exception_thrown() throws Exception {
        when(transactionCommandUseCase.createTransaction(any(UpsertTransactionRequestDTO.class))).thenThrow(
                new IllegalArgumentException("Transaction amount cannot be negative"));

        mockMvc.perform(post("/api/v1/transactions").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(upsertRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Transaction amount cannot be negative"));
    }

    @Test
    void should_return_updated_transaction_when_valid_update_request_given() throws Exception {
        when(transactionCommandUseCase.updateTransaction(anyString(), any(UpsertTransactionRequestDTO.class))).thenReturn(sampleTransaction);

        mockMvc.perform(put("/api/v1/transactions/test-id-123").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upsertRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.name").value("Test Transaction"));

        verify(transactionCommandUseCase, times(1)).updateTransaction(anyString(), any(UpsertTransactionRequestDTO.class));
    }

    @Test
    void should_return_not_found_when_transaction_does_not_exist() throws Exception {
        when(transactionCommandUseCase.updateTransaction(anyString(), any(UpsertTransactionRequestDTO.class))).thenThrow(
                new DataNotFoundException("Transaction not found with id: non-existent"));

        mockMvc.perform(put("/api/v1/transactions/non-existent").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upsertRequest)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Business exception"));
    }

    @Test
    void should_return_transaction_when_valid_id_given() throws Exception {
        when(transactionQueryUseCase.getTransactionById("test-id-123")).thenReturn(sampleTransaction);

        mockMvc.perform(get("/api/v1/transactions/test-id-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id-123"))
                .andExpect(jsonPath("$.name").value("Test Transaction"));

        verify(transactionQueryUseCase, times(1)).getTransactionById("test-id-123");
    }

    @Test
    void should_return_all_transactions_when_transactions_exist() throws Exception {
        Transaction transaction2 = Transaction.builder()
                .id("test-id-456")
                .name("Another Transaction")
                .amount(new BigDecimal("200.00")).category("Transport").type(TransactionType.WITHDRAW)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        List<Transaction> transactions = Arrays.asList(sampleTransaction, transaction2);
        when(transactionQueryUseCase.getPageTransactions(1, 10)).thenReturn(new Page<>(2, transactions));

        mockMvc.perform(get("/api/v1/transactions").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("test-id-123"))
                .andExpect(jsonPath("$.data[1].id").value("test-id-456"));

        verify(transactionQueryUseCase, times(1)).getPageTransactions(1, 10);
    }

    @Test
    void should_return_empty_list_when_no_transactions_exist() throws Exception {
        when(transactionQueryUseCase.getPageTransactions(1, 10)).thenReturn(new Page<>(0, List.of()));

        mockMvc.perform(get("/api/v1/transactions").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(transactionQueryUseCase, times(1)).getPageTransactions(1, 10);
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