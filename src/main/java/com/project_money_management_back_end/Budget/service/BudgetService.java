package com.project_money_management_back_end.budget.service;

import com.project_money_management_back_end.budget.dto.BudgetResponseDTO;
import com.project_money_management_back_end.budget.model.Budget;
import com.project_money_management_back_end.budget.repository.BudgetRepository;
import com.project_money_management_back_end.utils.EncryptionUtil;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@NoArgsConstructor
public class BudgetService {

    private BudgetRepository budgetRepository;

    private EncryptionUtil encryptionUtil;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, EncryptionUtil encryptionUtil) {
        this.budgetRepository = budgetRepository;
        this.encryptionUtil = encryptionUtil;
    }

    public BudgetResponseDTO getBudgetByUsername(String username) {
        Budget budget = budgetRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Budget not found for username: " + username));

        String decryptedAmount = null;

        try {
            String encryptedAmount = budget.getEncrypted_amount();

            decryptedAmount = encryptionUtil.decrypt(encryptedAmount);

        } catch (Exception e) {
            throw new RuntimeException("Error decrypting the budget for the user: " + username, e);
        }

        return new BudgetResponseDTO(budget.getUsername(), decryptedAmount);
    }
}
