package com.yamajun.moviextractor.controller

import com.yamajun.moviextractor.model.CrawlConfiguration
import com.yamajun.moviextractor.process.peliscon.PelisconProcessManager
import com.yamajun.moviextractor.repository.CrawlProcessRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping('/process')
class ProcessController {

    @Autowired CrawlProcessRepository crawlProcessRepository
    @Autowired PelisconProcessManager pelisconProcessManager

    @GetMapping('')
    def getProcess(){
        return crawlProcessRepository.findAll()
    }

    @GetMapping('/{processId}/start')
    def startProcess(@PathVariable('processId') String processId){
        pelisconProcessManager.startExtractionProcess(false)
    }

    @GetMapping('/{processId}/restart')
    def restartProcess(@PathVariable('processId') String processId){
        pelisconProcessManager.startExtractionProcess(true)
    }

    @PostMapping('/{processId}/crawler')
    ResponseEntity putCrawlerConfigIntoProcess(@PathVariable('processId') String processId,
                                               @RequestBody CrawlConfiguration crawlConfiguration){
        def crawlProcess = crawlProcessRepository.findById(processId);
        if(!crawlProcess){
            return ResponseEntity.badRequest().body('ProccesId not found');
        } else {
            crawlProcess.crawlConfiguration = crawlConfiguration;
            return ResponseEntity.ok().body('Ok')
        }
    }







}
