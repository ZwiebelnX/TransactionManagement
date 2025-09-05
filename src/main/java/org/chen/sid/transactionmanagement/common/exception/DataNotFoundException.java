package org.chen.sid.transactionmanagement.common.exception;

import org.chen.sid.transactionmanagement.common.exception.basic.BusinessException;
import org.springframework.http.HttpStatus;

public class DataNotFoundException extends BusinessException {
    public DataNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}