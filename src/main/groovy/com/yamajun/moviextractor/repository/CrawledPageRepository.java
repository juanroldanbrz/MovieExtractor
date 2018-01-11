package com.yamajun.moviextractor.repository;

import com.yamajun.moviextractor.model.CrawledPage;
import com.yamajun.moviextractor.model.CrawledPage.AnalyzedStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawledPageRepository extends MongoRepository<CrawledPage, String> {

  List<CrawledPage> findByAnalyzedStatusOrderByCreatedAtDesc(AnalyzedStatus analyzedStatus,
      Pageable pageable);

  CrawledPage findByNormalizedUrl(String normalizedUrl);
}
