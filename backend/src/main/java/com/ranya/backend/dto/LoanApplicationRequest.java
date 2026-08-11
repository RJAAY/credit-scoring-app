package com.ranya.backend.dto;

import com.ranya.backend.enums.EmploymentType;
import com.ranya.backend.enums.LoanType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {
    private LoanType typePret;
    private BigDecimal montantDemande;
    private Integer dureeMois;
    private Double tauxInteret;

    private BigDecimal revenuMensuel;
    private BigDecimal chargesMensuelles;
    private EmploymentType situationProfessionnelle;
    private Integer ancienneteMois;
    private BigDecimal apportPersonnel;
}