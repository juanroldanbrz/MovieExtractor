package com.yamajun.moviextractor.controller
import org.jsoup.nodes.Document
import com.yamajun.moviextractor.service.BootstrapService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BootstrapController {

    @Autowired
    BootstrapService bootstrapService

    @GetMapping('/bootstrap')
    def bootstrap(){
        bootstrapService.initPeliscomDomain()
        return [ 'status' : 'ok']
    }
}
