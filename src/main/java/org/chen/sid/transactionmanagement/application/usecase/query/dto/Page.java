package org.chen.sid.transactionmanagement.application.usecase.query.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Page<T> {
    private long total;

    private List<T> data;

    public Page(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }
}
