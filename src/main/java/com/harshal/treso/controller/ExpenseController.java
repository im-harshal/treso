package com.harshal.treso.controller;

import com.harshal.treso.model.Expense;
import com.harshal.treso.model.User;
import com.harshal.treso.repository.ExpenseRepository;
import com.harshal.treso.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;

    private final UserRepository userRepository;

    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Expense cerateExpense(@RequestBody Expense expense, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @GetMapping
    public List<Expense> getExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        return expenseRepository.findByUser(user, PageRequest.of(page, size)).getContent();
    }

    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @RequestBody Expense updatedExpense,
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("Expense not found for id: " + id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");});
        if(!expense.getUser().getId().equals(user.getId())){
           throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not allowed to update this expense");
        }

        expense.setAmount(updatedExpense.getAmount());
        expense.setCategory(updatedExpense.getCategory());
        expense.setDescription(updatedExpense.getDescription());
        expense.setDate(updatedExpense.getDate());
        return expenseRepository.save(expense);
    }

    @DeleteMapping("/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
            System.out.println("Expense not found for id: " + id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");});
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this expense");
        }
        expenseRepository.delete(expense);
        return "Expense has been deleted";
    }

}
