package com.project_money_management_back_end.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponseDTO {

    @Schema(description = "The username associated with the budget", example = "john_doe")
    private String username;

    @Schema(description = "The monetary amount of the budget", example = "\"3000.00\"", type = "string")
    private String amount;

}
