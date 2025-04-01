package com.project_money_management_back_end.transaction.service;

import com.project_money_management_back_end.budget.model.Budget;
import com.project_money_management_back_end.budget.repository.BudgetRepository;
import com.project_money_management_back_end.budget.service.BudgetService;
import com.project_money_management_back_end.transaction.dto.IncomeExpenseStatisticsDTO;
import com.project_money_management_back_end.transaction.dto.TransactionRequestCreatingDTO;
import com.project_money_management_back_end.transaction.dto.TransactionRequestUpdatingDTO;
import com.project_money_management_back_end.transaction.dto.TransactionResponseDTO;
import com.project_money_management_back_end.transaction.enums.TransactionType;
import com.project_money_management_back_end.transaction.model.Transaction;
import com.project_money_management_back_end.transaction.repository.TransactionRepository;
import com.project_money_management_back_end.utils.EncryptionUtil;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

@Service
@NoArgsConstructor
public class TransactionService {

    @Autowired
    private BudgetRepository budgetRepository;

    private TransactionRepository transactionRepository;

    private EncryptionUtil encryptionUtil;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, EncryptionUtil encryptionUtil) {
        this.transactionRepository = transactionRepository;
        this.encryptionUtil = encryptionUtil;
    }

    public TransactionResponseDTO saveTransaction(String username, TransactionRequestCreatingDTO transactionRequestCreatingDTO) throws Exception {
        // Fetch the Budget associated with the username
        Budget budget = budgetRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Budget not found for username: " + username));

        // Encrypt the name and amount before storing them in the database
        String encryptedName = encryptionUtil.encrypt(transactionRequestCreatingDTO.getName());
        String encryptedAmount = encryptionUtil.encrypt(transactionRequestCreatingDTO.getAmount());

        // Create the Transaction entity and associate it with the Budget
        Transaction transaction = new Transaction(
                null,  // Let the ID auto-generate
                transactionRequestCreatingDTO.getType(),
                encryptedName,
                encryptedAmount,
                budget  // Associate the transaction with the budget
        );

        budgetService.updateBudget(transaction);

        // Save the transaction in the database
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Decrypt and return the transaction details
        String decryptedName = encryptionUtil.decrypt(savedTransaction.getEncrypted_name());
        String decryptedAmount = encryptionUtil.decrypt(savedTransaction.getEncrypted_amount());

        return new TransactionResponseDTO(
                savedTransaction.getTransaction_id(),
                savedTransaction.getType(),
                decryptedName,
                decryptedAmount
        );
    }

    public TransactionResponseDTO getTransactionById(Long transactionId) throws Exception {
        // Retrieve the transaction from the database
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found for ID: " + transactionId));

        // Decrypt the name and amount
        String decryptedName = encryptionUtil.decrypt(transaction.getEncrypted_name());
        String decryptedAmount = encryptionUtil.decrypt(transaction.getEncrypted_amount());

        // Return the decrypted data in the response DTO
        return new TransactionResponseDTO(
                transaction.getTransaction_id(),
                transaction.getType(),
                decryptedName,
                decryptedAmount
        );
    }

    public TransactionResponseDTO updateTransaction(Long transactionId, TransactionRequestUpdatingDTO transactionRequestUpdatingDTO) throws Exception {
        // Retrieve the transaction by ID
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found for ID: " + transactionId));

        BigDecimal currentTransactionAmount = new BigDecimal(encryptionUtil.decrypt(transaction.getEncrypted_amount()));
        BigDecimal newTransactionAmount = new BigDecimal(transactionRequestUpdatingDTO.getAmount());

        budgetService.updateBudgetAfterTransactionChange(transaction, currentTransactionAmount, newTransactionAmount);


        // Update the fields with the new data
        transaction.setEncrypted_name(encryptionUtil.encrypt(transactionRequestUpdatingDTO.getName()));  // Encrypt the new name
        String amountAsString = transactionRequestUpdatingDTO.getAmount();
        transaction.setEncrypted_amount(encryptionUtil.encrypt(amountAsString));  // Encrypt the new amount

        // Save the updated transaction back to the database
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Decrypt the updated data before returning it
        String decryptedName = encryptionUtil.decrypt(updatedTransaction.getEncrypted_name());
        String decryptedAmount = encryptionUtil.decrypt(updatedTransaction.getEncrypted_amount());

        // Return the updated and decrypted transaction in the response DTO
        return new TransactionResponseDTO(
                updatedTransaction.getTransaction_id(),
                updatedTransaction.getType(),
                decryptedName,
                decryptedAmount
        );
    }

    public void deleteTransaction(Long transactionId) throws Exception {
        // Check if the transaction exists
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found for ID: " + transactionId));

        budgetService.updateBudgetForDeletion(transaction);

        // Delete the transaction
        transactionRepository.delete(transaction);
    }

    public IncomeExpenseStatisticsDTO getIncomeExpenseStatistics(String username) throws Exception {

        // Retrieve the user's budget
        Budget budget = budgetRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Budget not found for username: " + username));

        // Retrieve all income transactions
        BigDecimal totalIncome = transactionRepository.findAllByUsernameAndType(username, TransactionType.INCOME)
                .stream()
                .map(transaction -> {
                    try {
                        return new BigDecimal(encryptionUtil.decrypt(transaction.getEncrypted_amount()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String totalIncomeString = totalIncome.setScale(2, RoundingMode.HALF_UP).toString();

        // Retrieve all expense transactions
        BigDecimal totalExpenses = transactionRepository.findAllByUsernameAndType(username, TransactionType.EXPENSE)
                .stream()
                .map(transaction -> {
                    try {
                        return new BigDecimal(encryptionUtil.decrypt(transaction.getEncrypted_amount()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String totalExpensesString = totalExpenses.setScale(2, RoundingMode.HALF_UP).toString();

        // Calculate the combined total (income + expenses)
        BigDecimal combinedTotal = totalIncome.add(totalExpenses);

        // Calculate the percentages
        BigDecimal incomePercentage = combinedTotal.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalIncome.divide(combinedTotal, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal expensePercentage = combinedTotal.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : totalExpenses.divide(combinedTotal, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        String incomePercentageFormatted = incomePercentage.setScale(2, RoundingMode.HALF_UP) + "%";
        String expensePercentageFormatted = expensePercentage.setScale(2, RoundingMode.HALF_UP) + "%";

        return new IncomeExpenseStatisticsDTO(totalIncomeString, totalExpensesString, incomePercentageFormatted, expensePercentageFormatted);
    }
}
