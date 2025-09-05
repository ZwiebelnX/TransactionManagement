package org.chen.sid.transactionmanagement.common.exception;

import org.chen.sid.transactionmanagement.common.exception.basic.BusinessException;
import org.springframework.http.HttpStatus;

public class RequestArgumentIllegalException extends BusinessException {

    public RequestArgumentIllegalException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
