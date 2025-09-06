package org.chen.sid.transactionmanagement.application.validator;

import org.chen.sid.transactionmanagement.common.exception.RequestArgumentIllegalException;

public class CommonRequestParamValidator {
    private static final int MAX_PAGE_SIZE = 1000;

    public static void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RequestArgumentIllegalException("Transaction ID cannot be null or empty");
        }
    }

    public static void validatePaginationParameters(long page, long size) {
        if (page <= 0) {
            throw new RequestArgumentIllegalException("Page number must be greater than 0");
        }
        if (size <= 0) {
            throw new RequestArgumentIllegalException("Page size must be greater than 0");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new RequestArgumentIllegalException("Page size must be less than 1000");
        }
    }
}
