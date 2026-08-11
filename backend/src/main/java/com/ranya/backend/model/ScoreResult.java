package com.ranya.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_results")
@Data
public class ScoreResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer scoreGlobal;

    @Column(length = 1000)
    private String detailFacteurs;

    private LocalDateTime dateCalcul;

    @OneToOne
    @JoinColumn(name = "loan_application_id")
    private LoanApplication loanApplication;
}