package com.ranya.backend.model;

import com.ranya.backend.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "status_history")
@Data
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoanStatus ancienStatut;

    @Enumerated(EnumType.STRING)
    private LoanStatus nouveauStatut;

    private LocalDateTime dateChangement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User modifiePar;

    @ManyToOne
    @JoinColumn(name = "loan_application_id")
    private LoanApplication loanApplication;

    private String commentaire;
}