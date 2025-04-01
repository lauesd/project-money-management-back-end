package com.project_money_management_back_end.transaction.repository;

import com.project_money_management_back_end.transaction.enums.TransactionType;
import com.project_money_management_back_end.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.budget.username = :username AND t.type = :type")
    List<Transaction> findAllByUsernameAndType(@Param("username") String username, @Param("type") TransactionType type);
}
