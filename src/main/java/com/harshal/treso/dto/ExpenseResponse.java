package com.harshal.treso.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class ExpenseResponse {
    private Long id;
    private Double amount;
    private String category;
    private String description;
    private LocalDate date;
}
