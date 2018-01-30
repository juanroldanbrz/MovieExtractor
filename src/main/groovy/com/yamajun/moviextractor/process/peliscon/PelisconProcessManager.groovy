package com.yamajun.moviextractor.process.peliscon

import com.yamajun.cloudbypass.CHttpRequester
import com.yamajun.moviextractor.model.CrawlProcess
import com.yamajun.moviextractor.model.CrawledPage
import com.yamajun.moviextractor.model.CrawledPage.AnalyzedStatus
import com.yamajun.moviextractor.model.LinkType
import com.yamajun.moviextractor.repository.CrawlProcessRepository
import com.yamajun.moviextractor.repository.CrawledPageRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

import java.time.LocalDateTime
import java.util.concurrent.Executors

@Slf4j
@Component
class PelisconProcessManager {

    @Autowired
    CrawledPageRepository crawledPageRepository

    @Autowired
    CrawlProcessRepository crawlProcessRepository

    def pelisconHost = 'https://peliscon.com'
    def pelisconDomain = 'peliscon.com'

    def executor = Executors.newFixedThreadPool(1)

    def httpRequester = new CHttpRequester()

    def startExtractionProcess(boolean continueWithLastProcess) {
        executor.execute({
            log.info('Starting crawling process for {}', pelisconDomain)
            def initPagesToAnalyze
            if (continueWithLastProcess){
                def pelisconPage = new CrawledPage(
                        normalizedUrl: normalizeUrl(pelisconHost),
                        domain: pelisconDomain,
                        analyzedStatus: AnalyzedStatus.NOT_ANALYZED,
                        createdAt: LocalDateTime.now(),
                        updatedAt: LocalDateTime.now()
                )

                initPagesToAnalyze = [crawledPageRepository.save(pelisconPage)]
            } else {
                initPagesToAnalyze = getMoviesByAnalyzedStatus(AnalyzedStatus.NOT_ANALYZED, 20)
            }
            while (initPagesToAnalyze) {
                crawlPeliscon(initPagesToAnalyze)
                initPagesToAnalyze = getMoviesByAnalyzedStatus(AnalyzedStatus.NOT_ANALYZED, 20)
            }

            log.info('Finishing crawling process for {}', pelisconDomain)
            def crawlProcess = crawlProcessRepository.findByDomain(pelisconDomain)
            crawlProcess.processStatus = CrawlProcess.ProcessStatus.DONE
            crawlProcessRepository.save(crawlProcess)
            // write report
        })

        log.info('Changing process status to crawling for {}', pelisconDomain)
        def currentProcess = crawlProcessRepository.findByDomain(pelisconDomain)
        currentProcess.processStatus = CrawlProcess.ProcessStatus.CRAWLING
        crawlProcessRepository.save(currentProcess)
        return ['status': 'started_crawl']
    }

    def crawlPeliscon(List<CrawledPage> linksToCrawl) {
        linksToCrawl.each {
            log.info('Analyzing {}', it.normalizedUrl)
            def response
            try {
                response = httpRequester.get('https://' + it.normalizedUrl, 30000)
            } catch (Exception ignored) {
                def crawledPage = crawledPageRepository.findByNormalizedUrl(it.normalizedUrl)
                if (crawledPage) {
                    crawledPage.linkType = LinkType.COULDNT_BE_ANALYZED
                } else {
                    crawledPage = new CrawledPage(
                            normalizedUrl: it.normalizedUrl,
                            domain: pelisconDomain,
                            analyzedStatus: AnalyzedStatus.BAD_LINK,
                            createdAt: LocalDateTime.now(),
                            updatedAt: LocalDateTime.now(),
                    )
                }

                crawledPageRepository.save(crawledPage)
                log.info('Skipped {}', it.normalizedUrl)
                return
            }

            def linkType = getLinkType(it.normalizedUrl)

            it.analyzedStatus = AnalyzedStatus.ANALYZED
            it.updatedAt = LocalDateTime.now()
            it.linkType = linkType
            crawledPageRepository.save(it)

            def normalizedLinks = response.select('a')
                    .findAll { it.attributes.get('href') }
                    .collect { normalizeUrl(it.attr("abs:href")) }
                    .findAll { it.startsWith(pelisconDomain) }
                    .unique { a, b -> a <=> b }

            normalizedLinks.findAll {
                !crawledPageRepository.findByNormalizedUrl(it)
            }.each {
                crawledPageRepository.save(new CrawledPage(
                        normalizedUrl: it,
                        domain: pelisconDomain,
                        analyzedStatus: AnalyzedStatus.NOT_ANALYZED,
                        createdAt: LocalDateTime.now(),
                        updatedAt: LocalDateTime.now(),
                ))
            }
        }
    }

    static def getLinkType(String link) {
        if (!link.startsWith('peliscon.com/peliculas/page') &&
                link.startsWith('peliscon.com/peliculas')) return LinkType.MOVIE_LINK_CONTAINER
        else if (!link.startsWith('peliscon.com/episodios/page') &&
                link.startsWith('peliscon.com/episodios')) return LinkType.EPISODE_LINK_CONTAINER
        else return LinkType.OTHER_DATA
    }

    def getMoviesByAnalyzedStatus(AnalyzedStatus analyzedStatus, int limit) {
        return crawledPageRepository.findByAnalyzedStatusOrderByCreatedAtDesc(analyzedStatus,
                new PageRequest(0, limit))
    }

    static def normalizeUrl(String uriAsString) {
        def uri = new URI(uriAsString)
        def normalizedUri = uri.host
        if (uri.path != '/') {
            normalizedUri += uri.path
        }
        return normalizedUri
    }
}
