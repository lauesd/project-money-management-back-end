package com.project_money_management_back_end.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestUpdatingDTO {

    @Schema(description = "The name or description of the transaction (e.g., 'Salary')", example = "Salary", type = "string")
    private String name;

    @Schema(description = "The monetary amount of the transaction (e.g., '3000.00')", example = "\"3000.00\"", type = "string")
    private String amount;
}
