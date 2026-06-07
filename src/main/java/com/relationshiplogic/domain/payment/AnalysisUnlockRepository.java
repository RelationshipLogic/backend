package com.relationshiplogic.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisUnlockRepository extends JpaRepository<AnalysisUnlock, Long> {

    Optional<AnalysisUnlock> findBySessionIdAndGrade(Long sessionId, UnlockGrade grade);

    List<AnalysisUnlock> findAllBySessionId(Long sessionId);
}
