package com.ranya.backend.repository;

import com.ranya.backend.model.StatusHistory;
import com.ranya.backend.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByLoanApplicationOrderByDateChangementAsc(LoanApplication loanApplication);
}