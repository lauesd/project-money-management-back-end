package com.project_money_management_back_end.transaction.controller;

import com.project_money_management_back_end.transaction.dto.TransactionRequestCreatingDTO;
import com.project_money_management_back_end.transaction.dto.TransactionRequestUpdatingDTO;
import com.project_money_management_back_end.transaction.dto.TransactionResponseDTO;
import com.project_money_management_back_end.transaction.service.TransactionService;
import com.project_money_management_back_end.utils.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/transactions")

public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Operation(
            summary = "Create a new transaction",
            description = "Add a new income or spending transaction to the user's budget.",
            tags = {"Transactions"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created the transaction",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found for the specified username",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/{username}")
    public ResponseEntity<Object> createTransaction(@PathVariable String username, @RequestBody TransactionRequestCreatingDTO transactionRequestDTO) {
        try {
            // Call the service method to save the transaction
            TransactionResponseDTO createdTransaction = transactionService.saveTransaction(username, transactionRequestDTO);

            // Return the saved transaction as response with a 201 status
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (NoSuchElementException e) {
            // Handle case when the budget is not found (404)
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            // Handle general server errors (500)
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get transaction details by ID",
            description = "Retrieve transaction details, including decrypted name and amount, for a specific transaction ID.",
            tags = {"Transactions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the transaction",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found for the specified ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<Object> getTransaction(@PathVariable Long transactionId) {
        try {
            TransactionResponseDTO transactionResponse = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(transactionResponse);
        } catch (NoSuchElementException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(
            summary = "Update an existing transaction",
            description = "Update a transaction in the user's budget (either income or expense).",
            tags = {"Transactions"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated the transaction",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction or Budget not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PutMapping("/{transactionId}")
    public ResponseEntity<Object> updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionRequestUpdatingDTO transactionRequestUpdatingDTO
    ) {
        try {
            // Llamar al servicio para actualizar la transacción
            TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(transactionId, transactionRequestUpdatingDTO);

            // Retornar la transacción actualizada con un status 200
            return ResponseEntity.ok(updatedTransaction);
        } catch (NoSuchElementException e) {
            // Si la transacción o presupuesto no se encuentran, se retorna error 404
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            // Si ocurre algún otro error, se retorna error 500
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Delete a transaction",
            description = "Delete a transaction by its ID.",
            tags = {"Transactions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the transaction"),
            @ApiResponse(responseCode = "404", description = "Transaction not found for the specified ID"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable Long transactionId) {
        try {
            transactionService.deleteTransaction(transactionId);
            return ResponseEntity.ok("Transaction successfully deleted.");
        } catch (NoSuchElementException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
