package com.ranya.backend.model;

import com.ranya.backend.enums.EmploymentType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "financial_profiles")
@Data
public class FinancialProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal revenuMensuel;
    private BigDecimal chargesMensuelles;

    @Enumerated(EnumType.STRING)
    private EmploymentType situationProfessionnelle;

    private Integer ancienneteMois;
    private BigDecimal apportPersonnel;

    @OneToOne
    @JoinColumn(name = "loan_application_id")
    private LoanApplication loanApplication;
}