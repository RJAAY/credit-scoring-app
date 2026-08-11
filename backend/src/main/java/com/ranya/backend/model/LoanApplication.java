package com.ranya.backend.model;

import com.ranya.backend.enums.LoanStatus;
import com.ranya.backend.enums.LoanType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Data
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoanType typePret;

    private BigDecimal montantDemande;
    private Integer dureeMois;
    private Double tauxInteret;

    @Enumerated(EnumType.STRING)
    private LoanStatus statut;

    private LocalDateTime dateSoumission;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agentAssigne;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL)
    private FinancialProfile profilFinancier;

    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL)
    private ScoreResult resultatScore;
}