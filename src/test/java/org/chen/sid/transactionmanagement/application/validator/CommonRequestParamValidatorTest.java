package org.chen.sid.transactionmanagement.application.validator;

import org.chen.sid.transactionmanagement.common.exception.RequestArgumentIllegalException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CommonRequestParamValidatorTest {

    @Test
    void should_pass_validation_when_valid_id_given() {
        assertDoesNotThrow(() -> CommonRequestParamValidator.validateId("valid-id-123"));
    }

    @Test
    void should_throw_exception_when_null_id_given() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validateId(null))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Transaction ID cannot be null or empty");
    }

    @Test
    void should_throw_exception_when_empty_id_given() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validateId(""))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Transaction ID cannot be null or empty");
    }

    @Test
    void should_throw_exception_when_whitespace_only_id_given() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validateId("   "))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Transaction ID cannot be null or empty");
    }

    @Test
    void should_pass_validation_when_valid_pagination_parameters_given() {
        assertDoesNotThrow(() -> CommonRequestParamValidator.validatePaginationParameters(1, 10));
        assertDoesNotThrow(() -> CommonRequestParamValidator.validatePaginationParameters(1, 1000));
        assertDoesNotThrow(() -> CommonRequestParamValidator.validatePaginationParameters(100, 50));
    }

    @Test
    void should_throw_exception_when_page_is_zero() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validatePaginationParameters(0, 10))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page number must be greater than 0");
    }

    @Test
    void should_throw_exception_when_page_is_negative() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validatePaginationParameters(-1, 10))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page number must be greater than 0");
    }

    @Test
    void should_throw_exception_when_size_is_zero() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validatePaginationParameters(1, 0))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page size must be greater than 0");
    }

    @Test
    void should_throw_exception_when_size_is_negative() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validatePaginationParameters(1, -1))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page size must be greater than 0");
    }

    @Test
    void should_throw_exception_when_size_exceeds_max_limit() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validatePaginationParameters(1, 1001))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page size must be less than 1000");
    }

    @Test
    void should_throw_exception_when_both_page_and_size_are_invalid() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validatePaginationParameters(0, 0))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page number must be greater than 0");
    }

    @Test
    void should_throw_exception_when_page_invalid_and_size_exceeds_limit() {
        assertThatThrownBy(() -> CommonRequestParamValidator.validatePaginationParameters(-1, 1001))
                .isInstanceOf(RequestArgumentIllegalException.class)
                .hasMessage("Page number must be greater than 0");
    }
}
