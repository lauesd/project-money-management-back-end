package com.project_money_management_back_end.budget.repository;

import com.project_money_management_back_end.budget.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, String> {
    Optional<Budget> findByUsername(String username);
}
