package com.ranya.backend.dto;

import com.ranya.backend.enums.LoanStatus;
import com.ranya.backend.enums.LoanType;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LoanApplicationResponse {
    private Long id;
    private LoanType typePret;
    private BigDecimal montantDemande;
    private LoanStatus statut;
    private Integer scoreGlobal;
    private String detailFacteurs;
    private LocalDateTime dateSoumission;
}