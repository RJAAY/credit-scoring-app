package com.ranya.backend.dto;

import com.ranya.backend.enums.LoanStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private LoanStatus nouveauStatut;
    private String commentaire;
}