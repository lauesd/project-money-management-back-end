package com.project_money_management_back_end.budget.dto;

import com.project_money_management_back_end.transaction.dto.TransactionResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponseDTO {

    @Schema(description = "The username associated with the budget", example = "john_doe")
    private String username;

    @Schema(description = "The monetary amount of the budget", example = "\"3000.00\"", type = "string")
    private String amount;

    @Schema(description = "List of income transactions", example = "[{\"transactionId\": 1, \"type\": \"INCOME\", \"name\": \"Salary\", \"amount\": \"3000.00\"}]")
    private List<TransactionResponseDTO> incomeTransactions;

    @Schema(description = "List of expense transactions", example = "[{\"transactionId\": 2, \"type\": \"EXPENSE\", \"name\": \"Groceries\", \"amount\": \"100.00\"}]")
    private List<TransactionResponseDTO> expenseTransactions;

}
