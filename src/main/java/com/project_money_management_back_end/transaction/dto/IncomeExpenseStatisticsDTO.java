package com.project_money_management_back_end.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncomeExpenseStatisticsDTO {

    @Schema(description = "Total income amount", example = "\"6000.00\"", type = "string")
    private String totalIncome;

    @Schema(description = "Total expenses amount", example = "\"3000.00\"", type = "string")
    private String totalExpenses;

    @Schema(description = "Percentage of income relative to the budget", example = "\"66.67%\"", type = "string")
    private String incomePercentage;

    @Schema(description = "Percentage of expenses relative to the budget", example = "\"33.33%\"", type = "string")
    private String expensePercentage;

}
