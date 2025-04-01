package com.project_money_management_back_end.budget.service;

import com.project_money_management_back_end.budget.dto.BudgetResponseDTO;
import com.project_money_management_back_end.budget.model.Budget;
import com.project_money_management_back_end.budget.repository.BudgetRepository;
import com.project_money_management_back_end.transaction.dto.TransactionResponseDTO;
import com.project_money_management_back_end.transaction.enums.TransactionType;
import com.project_money_management_back_end.transaction.model.Transaction;
import com.project_money_management_back_end.transaction.repository.TransactionRepository;
import com.project_money_management_back_end.utils.EncryptionUtil;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@NoArgsConstructor
public class BudgetService {

    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private EncryptionUtil encryptionUtil;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, EncryptionUtil encryptionUtil) {
        this.budgetRepository = budgetRepository;
        this.encryptionUtil = encryptionUtil;
    }

    public BudgetResponseDTO getBudgetByUsername(String username) throws Exception {
        Budget budget = budgetRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Budget not found for username: " + username));

        List<Transaction> incomeTransactions = transactionRepository.findAllByUsernameAndType(username, TransactionType.INCOME);
        List<Transaction> expenseTransactions = transactionRepository.findAllByUsernameAndType(username, TransactionType.EXPENSE);

        List<TransactionResponseDTO> incomeTransactionDTOs = incomeTransactions.stream()
                .map(transaction -> {
                    try {
                        return new TransactionResponseDTO(
                                transaction.getTransaction_id(),
                                transaction.getType(),
                                encryptionUtil.decrypt(transaction.getEncrypted_name()),
                                encryptionUtil.decrypt(transaction.getEncrypted_amount()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<TransactionResponseDTO> expenseTransactionDTOs = expenseTransactions.stream()
                .map(transaction -> {
                    try {
                        return new TransactionResponseDTO(
                                transaction.getTransaction_id(),
                                transaction.getType(),
                                encryptionUtil.decrypt(transaction.getEncrypted_name()),
                                encryptionUtil.decrypt(transaction.getEncrypted_amount()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        String decryptedAmount = encryptionUtil.decrypt(budget.getEncrypted_amount());


        return new BudgetResponseDTO(
                budget.getUsername(),
                decryptedAmount,
                incomeTransactionDTOs,
                expenseTransactionDTOs
        );
    }

    public void updateBudget(Transaction transaction) throws Exception {
        // Find the user’s current budget based on the username from the transaction
        Budget budget = budgetRepository.findByUsername(transaction.getBudget().getUsername())
                .orElseThrow(() -> new NoSuchElementException(
                        "Budget not found for username: " + transaction.getBudget().getUsername()));

        // Decrypt current budget amount
        BigDecimal currentAmount = new BigDecimal(encryptionUtil.decrypt(budget.getEncrypted_amount()));

        // Decrypt the transaction amount
        BigDecimal transactionAmount = new BigDecimal(encryptionUtil.decrypt(transaction.getEncrypted_amount()));

        // Update budget based on transaction type
        if (transaction.getType() == TransactionType.INCOME) {
            // Add the transaction amount to the budget if it's an income
            currentAmount = currentAmount.add(transactionAmount);
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            // Subtract the transaction amount from the budget if it's an expense
            currentAmount = currentAmount.subtract(transactionAmount);
        }

        // Re-encrypt the updated budget amount
        String updatedAmount = encryptionUtil.encrypt(currentAmount.toString());

        // Update the user's budget in the database
        budget.setEncrypted_amount(updatedAmount);
        budgetRepository.save(budget);
    }

    public void updateBudgetAfterTransactionChange(
            Transaction transaction,
            BigDecimal currentTransactionAmount,
            BigDecimal newTransactionAmount
    ) throws Exception {

        Budget budget = budgetRepository.findByUsername(transaction.getBudget().getUsername())
                .orElseThrow(() -> new NoSuchElementException("Budget not found for username: " + transaction.getBudget().getUsername()));

        // Decrypt the current budget amount
        BigDecimal budgetAmount = new BigDecimal(encryptionUtil.decrypt(budget.getEncrypted_amount()));

        // Update budget based on transaction type change
        if (transaction.getType() == TransactionType.INCOME) {
            budgetAmount = budgetAmount.subtract(currentTransactionAmount);  // Remove the old amount
            budgetAmount = budgetAmount.add(newTransactionAmount);  // Add the new amount
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            budgetAmount = budgetAmount.add(currentTransactionAmount);  // Remove the old amount
            budgetAmount = budgetAmount.subtract(newTransactionAmount);  // Subtract the new amount
        }

        // Re-encrypt updated budget amount
        String updatedAmount = encryptionUtil.encrypt(budgetAmount.toString());

        // Update the user's budget
        budget.setEncrypted_amount(updatedAmount);
        budgetRepository.save(budget);
    }


    public void updateBudgetForDeletion(Transaction transaction) throws Exception {
        // Find the user's current budget
        Budget budget = budgetRepository.findByUsername(transaction.getBudget().getUsername())
                .orElseThrow(() -> new NoSuchElementException("Budget not found for username"));

        // Decrypt current budget amount
        BigDecimal currentAmount = new BigDecimal(encryptionUtil.decrypt(budget.getEncrypted_amount()));

        // Convert transaction amount
        BigDecimal transactionAmount = new BigDecimal(encryptionUtil.decrypt(transaction.getEncrypted_amount()));

        // Reverse budget changes based on transaction type
        if (transaction.getType() == TransactionType.INCOME) {
            currentAmount = currentAmount.subtract(transactionAmount);
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            currentAmount = currentAmount.add(transactionAmount);
        }

        // Re-encrypt updated budget amount
        String updatedAmount = encryptionUtil.encrypt(currentAmount.toString());

        // Update the user's budget
        budget.setEncrypted_amount(updatedAmount);
        budgetRepository.save(budget);
    }
}
