package com.yamajun.moviextractor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime

@Document
class CrawledPage {

  enum AnalyzedStatus {
    NOT_ANALYZED,
    ANALYZED,
    BAD_LINK
  }

  @Id
  String id
  String normalizedUrl
  String domain
  AnalyzedStatus analyzedStatus = AnalyzedStatus.NOT_ANALYZED
  LinkType linkType
  LocalDateTime createdAt
  LocalDateTime updatedAt
}
