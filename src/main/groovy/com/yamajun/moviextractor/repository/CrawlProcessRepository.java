package com.yamajun.moviextractor.repository;

import com.yamajun.moviextractor.model.CrawlProcess;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlProcessRepository extends MongoRepository<CrawlProcess, String>{

  CrawlProcess findByDomain(String domain);
}
