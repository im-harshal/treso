package com.harshal.treso.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCreatedEvent {
    private Long expenseId;
    private Long userId;
    private Double amount;
    private String category;
    private LocalDate date;
}
