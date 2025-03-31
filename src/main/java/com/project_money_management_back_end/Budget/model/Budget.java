package com.project_money_management_back_end.budget.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Budget {

    @Id
    private String username;

    @Column(nullable = false)
    private String encrypted_amount;

}
