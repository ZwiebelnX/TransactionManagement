package org.chen.sid.transactionmanagement.integration;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.chen.sid.transactionmanagement.TransactionManagementApplication;
import org.chen.sid.transactionmanagement.adapter.in.dto.CreateTransactionRequestDTO;
import org.chen.sid.transactionmanagement.adapter.in.dto.UpdateTransactionRequestDTO;
import org.chen.sid.transactionmanagement.application.usecase.query.dto.Page;
import org.chen.sid.transactionmanagement.domain.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TransactionManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void should_create_transaction_successfully_when_valid_request_given() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO();
        request.setName("Test Transaction");
        request.setAmount(new BigDecimal("100.50"));

        Transaction createdTransaction = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201)
                .extract()
                .as(Transaction.class);

        assertThat(createdTransaction).isNotNull();
        assertThat(createdTransaction.getId()).isNotNull();
        assertThat(createdTransaction.getName()).isEqualTo("Test Transaction");
        assertThat(createdTransaction.getAmount()).isEqualTo(new BigDecimal("100.50"));
        assertThat(createdTransaction.getCreateTime()).isNotNull();
        assertThat(createdTransaction.getUpdateTime()).isNotNull();
    }

    @Test
    void should_return_bad_request_when_creating_transaction_with_invalid_data() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO();
        request.setName("");
        request.setAmount(new BigDecimal("-50.00"));

        String response = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(400)
                .extract()
                .asString();

        assertThat(response).contains("Validation Error");
        assertThat(response).contains("Transaction name cannot be null or empty");
        assertThat(response).contains("Transaction amount cannot be negative");
    }

    @Test
    void should_return_bad_request_when_creating_transaction_with_null_amount() {
        CreateTransactionRequestDTO request = new CreateTransactionRequestDTO();
        request.setName("Valid Name");
        request.setAmount(null);

        String response = given().contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(400)
                .extract()
                .asString();

        assertThat(response).contains("Validation Error");
    }

    @Test
    void should_update_transaction_successfully_when_valid_request_given() {
        CreateTransactionRequestDTO createRequest = new CreateTransactionRequestDTO();
        createRequest.setName("Original Transaction");
        createRequest.setAmount(new BigDecimal("100.00"));

        String transactionId = given().contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        UpdateTransactionRequestDTO updateRequest = new UpdateTransactionRequestDTO();
        updateRequest.setName("Updated Transaction");
        updateRequest.setAmount(new BigDecimal("200.00"));

        Transaction updatedTransaction = given().contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/v1/transactions/{id}", transactionId)
                .then()
                .statusCode(200)
                .extract()
                .as(Transaction.class);

        assertThat(updatedTransaction).isNotNull();
        assertThat(updatedTransaction.getId()).isEqualTo(transactionId);
        assertThat(updatedTransaction.getName()).isEqualTo("Updated Transaction");
        assertThat(updatedTransaction.getAmount()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    void should_return_not_found_when_updating_non_existent_transaction() {
        UpdateTransactionRequestDTO updateRequest = new UpdateTransactionRequestDTO();
        updateRequest.setName("Updated Transaction");
        updateRequest.setAmount(new BigDecimal("200.00"));

        String response = given().contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/v1/transactions/{id}", "non-existent-id")
                .then()
                .statusCode(404)
                .extract()
                .asString();

        assertThat(response).contains("Business exception");
    }

    @Test
    void should_return_bad_request_when_updating_transaction_with_negative_amount() {
        CreateTransactionRequestDTO createRequest = new CreateTransactionRequestDTO();
        createRequest.setName("Original Transaction");
        createRequest.setAmount(new BigDecimal("100.00"));

        String transactionId = given().contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        UpdateTransactionRequestDTO updateRequest = new UpdateTransactionRequestDTO();
        updateRequest.setName("Updated Transaction");
        updateRequest.setAmount(new BigDecimal("-100.00"));

        given().contentType(ContentType.JSON).body(updateRequest).when().put("/api/v1/transactions/{id}", transactionId).then().statusCode(400);
    }

    @Test
    void should_get_transaction_successfully_when_valid_id_given() {
        CreateTransactionRequestDTO createRequest = new CreateTransactionRequestDTO();
        createRequest.setName("Test Transaction");
        createRequest.setAmount(new BigDecimal("150.75"));

        String transactionId = given().contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        Transaction retrievedTransaction = given().when()
                .get("/api/v1/transactions/{id}", transactionId)
                .then()
                .statusCode(200)
                .extract()
                .as(Transaction.class);

        assertThat(retrievedTransaction).isNotNull();
        assertThat(retrievedTransaction.getId()).isEqualTo(transactionId);
        assertThat(retrievedTransaction.getName()).isEqualTo("Test Transaction");
        assertThat(retrievedTransaction.getAmount()).isEqualTo(new BigDecimal("150.75"));
    }

    @Test
    void should_return_internal_error_when_getting_non_existent_transaction() {
        String response = given().when().get("/api/v1/transactions/{id}", "non-existent-id").then().statusCode(500).extract().asString();

        assertThat(response).contains("Internal Server Error");
    }

    @Test
    void should_get_paginated_transactions_successfully() {
        for (int i = 1; i <= 3; i++) {
            CreateTransactionRequestDTO createRequest = new CreateTransactionRequestDTO();
            createRequest.setName("Test Transaction " + i);
            createRequest.setAmount(new BigDecimal(i * 100 + ".00"));

            given().contentType(ContentType.JSON).body(createRequest).when().post("/api/v1/transactions").then().statusCode(201);
        }

        Page<Transaction> page = given().param("page", 1)
                .param("size", 10)
                .when()
                .get("/api/v1/transactions")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(page).isNotNull();
        assertThat(page.getTotal()).isGreaterThan(0);
        assertThat(page.getData()).isNotEmpty();

        Transaction firstTransaction = page.getData().stream().findFirst().orElse(null);
        assertThat(firstTransaction).isNotNull();
    }

    @Test
    void should_get_empty_page_when_requesting_out_of_range_page() {
        Page<Transaction> page = given().param("page", 999)
                .param("size", 10)
                .when()
                .get("/api/v1/transactions")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        assertThat(page).isNotNull();
        assertThat(page.getTotal()).isGreaterThanOrEqualTo(0);
        assertThat(page.getData()).isEmpty();
    }

    @Test
    void should_delete_transaction_successfully_when_valid_id_given() {
        CreateTransactionRequestDTO createRequest = new CreateTransactionRequestDTO();
        createRequest.setName("Transaction to Delete");
        createRequest.setAmount(new BigDecimal("100.00"));

        String transactionId = given().contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/api/v1/transactions")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given().when().delete("/api/v1/transactions/{id}", transactionId).then().statusCode(204);

        given().when().get("/api/v1/transactions/{id}", transactionId).then().statusCode(500);
    }

    @Test
    void should_delete_successfully_even_when_transaction_does_not_exist() {
        given().when().delete("/api/v1/transactions/{id}", "non-existent-id").then().statusCode(204);
    }

    @Test
    void should_return_bad_request_when_page_is_zero() {
        given().param("page", 0).param("size", 10).when().get("/api/v1/transactions").then().statusCode(400);
    }

    @Test
    void should_return_bad_request_when_page_is_negative() {
        given().param("page", -1).param("size", 10).when().get("/api/v1/transactions").then().statusCode(400);
    }

    @Test
    void should_return_bad_request_when_size_is_zero() {
        given().param("page", 1).param("size", 0).when().get("/api/v1/transactions").then().statusCode(400);
    }

    @Test
    void should_return_bad_request_when_size_is_negative() {
        given().param("page", 1).param("size", -1).when().get("/api/v1/transactions").then().statusCode(400);
    }

    @Test
    void should_return_bad_request_when_both_page_and_size_are_invalid() {
        given().param("page", 0).param("size", 0).when().get("/api/v1/transactions").then().statusCode(400);
    }
}
