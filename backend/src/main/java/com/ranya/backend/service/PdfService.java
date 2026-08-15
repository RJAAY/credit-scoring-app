package com.ranya.backend.service;

import com.ranya.backend.model.LoanApplication;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private final AmortizationService amortizationService;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PdfService(AmortizationService amortizationService) {
        this.amortizationService = amortizationService;
    }

    public byte[] genererContrat(LoanApplication demande) throws IOException {

        List<AmortizationService.Echeance> tableau = amortizationService.genererTableau(
                demande.getMontantDemande(), demande.getTauxInteret(), demande.getDureeMois());

        PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        float marginX = 50;
        float startY = 780;
        float bottomLimit = 60;

        PDDocument document = new PDDocument();

        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(document, page);

            float y = startY;
            y = ecrireTitre(cs, fontBold, "Contrat de pret", marginX, y);
            y -= 20;
            y = ecrireLigne(cs, fontRegular, "Client : " + demande.getClient().getPrenom() + " " + demande.getClient().getNom(), marginX, y);
            y = ecrireLigne(cs, fontRegular, "Type de pret : " + demande.getTypePret(), marginX, y);
            y = ecrireLigne(cs, fontRegular, "Montant : " + demande.getMontantDemande() + " MAD", marginX, y);
            y = ecrireLigne(cs, fontRegular, "Duree : " + demande.getDureeMois() + " mois", marginX, y);
            y = ecrireLigne(cs, fontRegular, "Taux d'interet annuel : " + demande.getTauxInteret() + " %", marginX, y);
            y = ecrireLigne(cs, fontRegular, "Date : " + demande.getDateSoumission().format(DATE_FORMAT), marginX, y);

            y -= 25;
            y = ecrireTitre(cs, fontBold, "Tableau d'amortissement", marginX, y);
            y -= 15;
            y = ecrireLigneTableau(cs, fontBold, "Mois", "Mensualite", "Interet", "Capital", "Restant du", marginX, y);

            for (AmortizationService.Echeance e : tableau) {
                if (y < bottomLimit) {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);
                    y = startY;
                    y = ecrireLigneTableau(cs, fontBold, "Mois", "Mensualite", "Interet", "Capital", "Restant du", marginX, y);
                }
                y = ecrireLigneTableau(cs, fontRegular,
                        String.valueOf(e.mois()), e.mensualite().toPlainString(), e.interet().toPlainString(),
                        e.capital().toPlainString(), e.capitalRestant().toPlainString(), marginX, y);
            }

            cs.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();

        } finally {
            document.close();
        }
    }

    private float ecrireTitre(PDPageContentStream cs, PDType1Font font, String texte, float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, 16);
        cs.newLineAtOffset(x, y);
        cs.showText(texte);
        cs.endText();
        return y - 20;
    }

    private float ecrireLigne(PDPageContentStream cs, PDType1Font font, String texte, float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, 11);
        cs.newLineAtOffset(x, y);
        cs.showText(texte);
        cs.endText();
        return y - 16;
    }

    private float ecrireLigneTableau(PDPageContentStream cs, PDType1Font font,
                                     String c1, String c2, String c3, String c4, String c5,
                                     float x, float y) throws IOException {
        float[] offsets = {0, 60, 180, 300, 420};
        String[] valeurs = {c1, c2, c3, c4, c5};

        for (int i = 0; i < 5; i++) {
            cs.beginText();
            cs.setFont(font, 9);
            cs.newLineAtOffset(x + offsets[i], y);
            cs.showText(valeurs[i]);
            cs.endText();
        }
        return y - 14;
    }
}