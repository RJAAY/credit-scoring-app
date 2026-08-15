package com.ranya.backend.controller;

import com.ranya.backend.dto.LoanApplicationRequest;
import com.ranya.backend.dto.LoanApplicationResponse;
import com.ranya.backend.model.User;
import com.ranya.backend.service.LoanApplicationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.ranya.backend.dto.StatusUpdateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import org.springframework.http.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    public LoanApplicationController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @PostMapping
    public LoanApplicationResponse soumettre(@RequestBody LoanApplicationRequest request,
                                             @AuthenticationPrincipal User client) {
        return loanApplicationService.soumettre(request, client);
    }

    @GetMapping("/mes-demandes")
    public List<LoanApplicationResponse> mesDemandes(@AuthenticationPrincipal User client) {
        return loanApplicationService.mesDemandes(client);
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('AGENT_CREDIT') or hasRole('MANAGER')")
    public List<LoanApplicationResponse> demandesEnAttente() {
        return loanApplicationService.demandesEnAttente();
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('AGENT_CREDIT') or hasRole('MANAGER')")
    public LoanApplicationResponse changerStatut(@PathVariable Long id,
                                                 @RequestBody StatusUpdateRequest request,
                                                 @AuthenticationPrincipal User utilisateur) {
        return loanApplicationService.changerStatut(id, request, utilisateur);
    }

    @GetMapping("/{id}/contrat")
    public ResponseEntity<byte[]> telechargerContrat(@PathVariable Long id,
                                                     @AuthenticationPrincipal User utilisateur) throws IOException {
        byte[] pdf = loanApplicationService.genererContratPdf(id, utilisateur);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("contrat-pret-" + id + ".pdf").build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}