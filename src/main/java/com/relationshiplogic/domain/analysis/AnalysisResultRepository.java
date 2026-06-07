package com.relationshiplogic.domain.analysis;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AnalysisResultRepository extends MongoRepository<AnalysisResult, String> {

    Optional<AnalysisResult> findBySessionId(Long sessionId);
}
