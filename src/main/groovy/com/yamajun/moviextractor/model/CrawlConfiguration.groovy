package com.yamajun.moviextractor.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
class CrawlConfiguration {

    String entryPoint;
    String whiteList;
    String blackList;

}
