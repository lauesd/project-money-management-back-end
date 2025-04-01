package com.project_money_management_back_end.transaction.dto;

import com.project_money_management_back_end.transaction.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {

    @Schema(description = "The transaction ID", example = "1")
    private Long transactionId;

    @Schema(description = "The type of transaction", example = "INCOME")
    private TransactionType type;

    @Schema(description = "The name of the income/expense source", example = "Salary")
    private String name;

    @Schema(description = "The monetary amount for the transaction", example = "\"3000.00\"")
    private String amount;

}
