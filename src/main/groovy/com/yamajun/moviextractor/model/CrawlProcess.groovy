package com.yamajun.moviextractor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.LocalDateTime

@Document
class CrawlProcess {

    enum ProcessStatus {
        NOT_INICIATED,
        CRAWLING,
        DONE
    }

    @Id String id

    String domain
    LocalDateTime lastCrawled
    ProcessStatus processStatus
}
