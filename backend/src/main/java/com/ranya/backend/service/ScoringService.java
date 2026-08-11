package com.ranya.backend.service;

import com.ranya.backend.dto.LoanApplicationRequest;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

@Service
public class ScoringService {

    public record ScoreResultData(int score, String detail) {}

    public ScoreResultData calculerScore(LoanApplicationRequest request) {
        StringBuilder detail = new StringBuilder();
        int score = 0;

        // 1. Taux d'endettement (40 points max) — le facteur le plus lourd
        double tauxEndettement = request.getChargesMensuelles()
                .divide(request.getRevenuMensuel(), 4, RoundingMode.HALF_UP)
                .doubleValue();

        int pointsEndettement;
        if (tauxEndettement <= 0.30) pointsEndettement = 40;
        else if (tauxEndettement <= 0.40) pointsEndettement = 25;
        else if (tauxEndettement <= 0.50) pointsEndettement = 10;
        else pointsEndettement = 0;

        score += pointsEndettement;
        detail.append(String.format("Taux d'endettement %.0f%% : %d/40 pts. ", tauxEndettement * 100, pointsEndettement));

        // 2. Situation professionnelle (25 points max)
        int pointsEmploi = switch (request.getSituationProfessionnelle()) {
            case CDI -> 25;
            case CDD -> 15;
            case INDEPENDANT -> 10;
            case SANS_EMPLOI -> 0;
        };
        score += pointsEmploi;
        detail.append(String.format("Situation %s : %d/25 pts. ", request.getSituationProfessionnelle(), pointsEmploi));

        // 3. Ancienneté professionnelle (15 points max)
        int anciennete = request.getAncienneteMois();
        int pointsAnciennete;
        if (anciennete >= 36) pointsAnciennete = 15;
        else if (anciennete >= 12) pointsAnciennete = 10;
        else pointsAnciennete = 5;

        score += pointsAnciennete;
        detail.append(String.format("Ancienneté %d mois : %d/15 pts. ", anciennete, pointsAnciennete));

        // 4. Apport personnel (20 points max)
        double ratioApport = request.getApportPersonnel()
                .divide(request.getMontantDemande(), 4, RoundingMode.HALF_UP)
                .doubleValue();

        int pointsApport;
        if (ratioApport >= 0.20) pointsApport = 20;
        else if (ratioApport >= 0.10) pointsApport = 12;
        else if (ratioApport > 0) pointsApport = 5;
        else pointsApport = 0;

        score += pointsApport;
        detail.append(String.format("Apport %.0f%% du montant : %d/20 pts.", ratioApport * 100, pointsApport));

        return new ScoreResultData(score, detail.toString());
    }
}