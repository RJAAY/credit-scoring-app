package com.ranya.backend.service;

import com.ranya.backend.dto.LoanApplicationRequest;
import com.ranya.backend.dto.LoanApplicationResponse;
import com.ranya.backend.enums.LoanStatus;
import com.ranya.backend.model.*;
import com.ranya.backend.repository.LoanApplicationRepository;
import com.ranya.backend.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;
import com.ranya.backend.enums.Role;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import com.ranya.backend.dto.StatusUpdateRequest;
import com.ranya.backend.exception.ResourceNotFoundException;
import com.ranya.backend.exception.InvalidTransitionException;
import com.ranya.backend.repository.LoanApplicationRepository;
import org.springframework.security.access.AccessDeniedException;
import java.io.IOException;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final ScoringService scoringService;
    private final PdfService pdfService;

    public LoanApplicationService(LoanApplicationRepository loanApplicationRepository,
                                  StatusHistoryRepository statusHistoryRepository,
                                  ScoringService scoringService ,PdfService pdfService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.scoringService = scoringService;
        this.pdfService = pdfService;
    }

    public LoanApplicationResponse soumettre(LoanApplicationRequest request, User client) {

        // 1. Construire le profil financier
        FinancialProfile profil = new FinancialProfile();
        profil.setRevenuMensuel(request.getRevenuMensuel());
        profil.setChargesMensuelles(request.getChargesMensuelles());
        profil.setSituationProfessionnelle(request.getSituationProfessionnelle());
        profil.setAncienneteMois(request.getAncienneteMois());
        profil.setApportPersonnel(request.getApportPersonnel());

        // 2. Construire la demande
        LoanApplication demande = new LoanApplication();
        demande.setTypePret(request.getTypePret());
        demande.setMontantDemande(request.getMontantDemande());
        demande.setDureeMois(request.getDureeMois());
        demande.setTauxInteret(request.getTauxInteret());
        demande.setDateSoumission(LocalDateTime.now());
        demande.setClient(client);
        demande.setStatut(LoanStatus.SOUMISE);

        profil.setLoanApplication(demande);
        demande.setProfilFinancier(profil);

        // 3. Calculer le score
        ScoringService.ScoreResultData resultatCalcul = scoringService.calculerScore(request);

        ScoreResult score = new ScoreResult();
        score.setScoreGlobal(resultatCalcul.score());
        score.setDetailFacteurs(resultatCalcul.detail());
        score.setDateCalcul(LocalDateTime.now());
        score.setLoanApplication(demande);
        demande.setResultatScore(score);

        // 4. Déterminer le nouveau statut selon le score
        LoanStatus ancienStatut = demande.getStatut();
        LoanStatus nouveauStatut;
        if (resultatCalcul.score() >= 70) nouveauStatut = LoanStatus.APPROUVEE;
        else if (resultatCalcul.score() >= 40) nouveauStatut = LoanStatus.EN_ANALYSE;
        else nouveauStatut = LoanStatus.REJETEE;

        demande.setStatut(nouveauStatut);

        // 5. Sauvegarder (cascade = ALL sauvegarde aussi profil + score automatiquement)
        LoanApplication saved = loanApplicationRepository.save(demande);

        // 6. Historiser le changement de statut
        StatusHistory historique = new StatusHistory();
        historique.setLoanApplication(saved);
        historique.setAncienStatut(ancienStatut);
        historique.setNouveauStatut(nouveauStatut);
        historique.setDateChangement(LocalDateTime.now());
        historique.setModifiePar(client);
        historique.setCommentaire("Évaluation automatique par le moteur de scoring");
        statusHistoryRepository.save(historique);

        return new LoanApplicationResponse(saved.getId(), saved.getTypePret(), saved.getMontantDemande(),
                saved.getStatut(), resultatCalcul.score(), resultatCalcul.detail(), saved.getDateSoumission());
    }

    public List<LoanApplicationResponse> mesDemandes(User client) {
        return loanApplicationRepository.findByClient(client).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LoanApplicationResponse> demandesEnAttente() {
        return loanApplicationRepository.findByStatut(LoanStatus.EN_ANALYSE).stream()
                .map(this::toResponse)
                .toList();
    }

    public LoanApplicationResponse changerStatut(Long demandeId, StatusUpdateRequest request, User utilisateur) {
        LoanApplication demande = loanApplicationRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));

        LoanStatus ancienStatut = demande.getStatut();
        LoanStatus nouveauStatut = request.getNouveauStatut();

        if (!transitionAutorisee(ancienStatut, nouveauStatut, utilisateur.getRole())) {
            throw new InvalidTransitionException("Transition non autorisée : " + ancienStatut + " → " + nouveauStatut
                    + " pour le rôle " + utilisateur.getRole());
        }

        demande.setStatut(nouveauStatut);
        LoanApplication saved = loanApplicationRepository.save(demande);

        StatusHistory historique = new StatusHistory();
        historique.setLoanApplication(saved);
        historique.setAncienStatut(ancienStatut);
        historique.setNouveauStatut(nouveauStatut);
        historique.setDateChangement(LocalDateTime.now());
        historique.setModifiePar(utilisateur);
        historique.setCommentaire(request.getCommentaire());
        statusHistoryRepository.save(historique);

        return toResponse(saved);
    }

    private boolean transitionAutorisee(LoanStatus actuel, LoanStatus nouveau, Role role) {
        Set<LoanStatus> transitionsPossibles = switch (actuel) {
            case EN_ANALYSE -> Set.of(LoanStatus.APPROUVEE, LoanStatus.REJETEE);
            case APPROUVEE -> Set.of(LoanStatus.DECAISSEE);
            default -> Set.of();
        };

        if (!transitionsPossibles.contains(nouveau)) return false;

        // Seul un manager peut décaisser (validation finale)
        if (nouveau == LoanStatus.DECAISSEE && role != Role.MANAGER) return false;

        return true;
    }

    private LoanApplicationResponse toResponse(LoanApplication demande) {
        return new LoanApplicationResponse(
                demande.getId(), demande.getTypePret(), demande.getMontantDemande(),
                demande.getStatut(), demande.getResultatScore().getScoreGlobal(),
                demande.getResultatScore().getDetailFacteurs(), demande.getDateSoumission()
        );
    }
    public byte[] genererContratPdf(Long demandeId, User utilisateur) throws IOException {
        LoanApplication demande = loanApplicationRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande introuvable"));

        boolean estProprietaire = demande.getClient().getId().equals(utilisateur.getId());
        boolean estAgentOuManager = utilisateur.getRole() == Role.AGENT_CREDIT || utilisateur.getRole() == Role.MANAGER;

        if (!estProprietaire && !estAgentOuManager) {
            throw new AccessDeniedException("Vous n'avez pas accès à ce contrat.");
        }

        if (demande.getStatut() != LoanStatus.APPROUVEE && demande.getStatut() != LoanStatus.DECAISSEE) {
            throw new InvalidTransitionException("Le contrat n'est disponible que pour une demande approuvée ou décaissée.");
        }

        return pdfService.genererContrat(demande);
    }
}