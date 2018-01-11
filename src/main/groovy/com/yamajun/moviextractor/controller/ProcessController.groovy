package com.yamajun.moviextractor.controller

import com.yamajun.moviextractor.process.peliscon.PelisconProcessManager
import com.yamajun.moviextractor.repository.CrawlProcessRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProcessController {

    @Autowired CrawlProcessRepository crawlProcessRepository
    @Autowired PelisconProcessManager pelisconProcessManager

    @GetMapping('/{processId}/start')
    def startProcess(@PathVariable('processId') String processId){
        pelisconProcessManager.startExtractionProcess()
    }

    @GetMapping('(/process')
    def getProcess(){
        return crawlProcessRepository.findAll()
    }


}
