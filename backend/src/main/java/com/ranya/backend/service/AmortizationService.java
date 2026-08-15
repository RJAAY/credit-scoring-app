package com.ranya.backend.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmortizationService {

    public record Echeance(int mois, BigDecimal mensualite, BigDecimal interet,
                           BigDecimal capital, BigDecimal capitalRestant) {}

    public List<Echeance> genererTableau(BigDecimal montant, double tauxAnnuel, int dureeMois) {
        List<Echeance> tableau = new ArrayList<>();

        double tauxMensuel = tauxAnnuel / 100 / 12;
        BigDecimal mensualite = calculerMensualite(montant, tauxMensuel, dureeMois);

        BigDecimal capitalRestant = montant;

        for (int mois = 1; mois <= dureeMois; mois++) {
            BigDecimal interet = capitalRestant.multiply(BigDecimal.valueOf(tauxMensuel))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal capitalRembourse = mensualite.subtract(interet).setScale(2, RoundingMode.HALF_UP);
            capitalRestant = capitalRestant.subtract(capitalRembourse).setScale(2, RoundingMode.HALF_UP);

            if (capitalRestant.compareTo(BigDecimal.ZERO) < 0) capitalRestant = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

            tableau.add(new Echeance(mois, mensualite, interet, capitalRembourse, capitalRestant));
        }

        return tableau;
    }

    private BigDecimal calculerMensualite(BigDecimal montant, double tauxMensuel, int dureeMois) {
        if (tauxMensuel == 0) {
            return montant.divide(BigDecimal.valueOf(dureeMois), 2, RoundingMode.HALF_UP);
        }
        double m = montant.doubleValue() * tauxMensuel / (1 - Math.pow(1 + tauxMensuel, -dureeMois));
        return BigDecimal.valueOf(m).setScale(2, RoundingMode.HALF_UP);
    }
}