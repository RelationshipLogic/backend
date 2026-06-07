package com.relationshiplogic.domain.archive;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnalysisArchiveRepository extends MongoRepository<AnalysisArchive, String> {

    List<AnalysisArchive> findAllByUserIdOrderBySavedAtDesc(Long userId);

    List<AnalysisArchive> findAllByUserIdAndPersonAliasOrderBySavedAtDesc(Long userId, String personAlias);
}
