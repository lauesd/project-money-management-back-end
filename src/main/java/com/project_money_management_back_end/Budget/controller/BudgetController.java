package com.project_money_management_back_end.budget.controller;

import com.project_money_management_back_end.budget.dto.BudgetResponseDTO;
import com.project_money_management_back_end.budget.service.BudgetService;
import com.project_money_management_back_end.utils.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/budget")

public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Operation(summary = "Get budget for a specific user",
            description = "Retrieve the budget details of the user including the username and the budget amount.",
            tags = {"Budget"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the budget",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BudgetResponseDTO.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Budget not found for the specified username"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<Object> getBudget(@PathVariable String username) {
        try {
            BudgetResponseDTO budgetDTO = budgetService.getBudgetByUsername(username);
            return ResponseEntity.ok(budgetDTO);
        } catch (NoSuchElementException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
