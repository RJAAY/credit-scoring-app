package com.ranya.backend.repository;

import com.ranya.backend.model.FinancialProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialProfileRepository extends JpaRepository<FinancialProfile, Long> {
}