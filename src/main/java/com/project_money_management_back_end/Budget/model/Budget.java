package com.project_money_management_back_end.budget.model;

import com.project_money_management_back_end.transaction.model.Transaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Budget {

    @Id
    private String username;

    @Column(nullable = false)
    private String encrypted_amount;

    @OneToMany(mappedBy = "budget") // Relación bidireccional
    private List<Transaction> transactions;
}
