package com.yamajun.moviextractor.service

import com.yamajun.moviextractor.model.CrawlProcess
import com.yamajun.moviextractor.repository.CrawlProcessRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BootstrapService {

    @Autowired
    CrawlProcessRepository crawlProcessRepository

    def initPeliscomDomain(){
        CrawlProcess pelisconProcess = new CrawlProcess(domain: 'peliscon.com', processStatus:
                CrawlProcess.ProcessStatus.NOT_INICIATED)
        crawlProcessRepository.save(pelisconProcess)
    }
}
