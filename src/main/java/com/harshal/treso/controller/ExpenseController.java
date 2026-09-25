package com.harshal.treso.controller;

import com.harshal.treso.dto.ExpenseRequest;
import com.harshal.treso.dto.ExpenseResponse;
import com.harshal.treso.model.Expense;
import com.harshal.treso.model.User;
import com.harshal.treso.repository.ExpenseRepository;
import com.harshal.treso.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import com.harshal.treso.event.ExpenseCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository, 
                             KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ExpenseResponse createExpense(@Valid @RequestBody ExpenseRequest expenseRequest, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Expense expense = Expense.builder()
                .amount(expenseRequest.getAmount())
                .category(expenseRequest.getCategory())
                .description(expenseRequest.getDescription())
                .date(expenseRequest.getDate())
                .user(user)
                .build();
        Expense saved = expenseRepository.save(expense);

        // Phase 3: Emit Kafka Event
        try {
            ExpenseCreatedEvent event = new ExpenseCreatedEvent(
                    saved.getId(), user.getId(), saved.getAmount(), saved.getCategory(), saved.getDate()
            );
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("treso.expenses.events", eventJson);
        } catch (Exception e) {
            // In a real app, you might use an Outbox pattern here. For now, just log it.
            System.err.println("Failed to send Kafka event: " + e.getMessage());
        }

        return mapToExpenseResponse(saved);
    }

    @GetMapping
    public List<ExpenseResponse> getExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        List<Expense> expenses = expenseRepository.findByUser(user, PageRequest.of(page, size)).getContent();
        return expenses.stream().map(this::mapToExpenseResponse).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest updatedExpense,
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        if(!expense.getUser().getId().equals(user.getId())){
           throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not allowed to update this expense");
        }
        expense.setAmount(updatedExpense.getAmount());
        expense.setCategory(updatedExpense.getCategory());
        expense.setDescription(updatedExpense.getDescription());
        expense.setDate(updatedExpense.getDate());
        Expense saved = expenseRepository.save(expense);
        return mapToExpenseResponse(saved);
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this expense");
        }
        expenseRepository.delete(expense);
        return "Expense has been deleted";
    }

    private ExpenseResponse mapToExpenseResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setAmount(expense.getAmount());
        response.setCategory(expense.getCategory());
        response.setDescription(expense.getDescription());
        response.setDate(expense.getDate());
        return response;
    }
}
