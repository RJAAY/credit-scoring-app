package com.ranya.backend.repository;

import com.ranya.backend.model.LoanApplication;
import com.ranya.backend.model.User;
import com.ranya.backend.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByClient(User client);
    List<LoanApplication> findByStatut(LoanStatus statut);
    List<LoanApplication> findByAgentAssigne(User agent);
}