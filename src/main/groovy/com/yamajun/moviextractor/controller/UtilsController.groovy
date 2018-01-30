package com.yamajun.moviextractor.controller

import com.yamajun.cloudbypass.CHttpRequester
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping('/utils')
class UtilsController {

    def httpRequester = new CHttpRequester()

    @GetMapping('/test-connection')
    def bootstrap(@RequestParam('url') String url){
        try {
            boolean  isBehindFirewall = httpRequester.isBehindFirewall(url, 5000)
            return ['status' : 'alive', 'isBehindFirewall' : isBehindFirewall]
        } catch (Exception ignored){
            return ['status' : 'dead']
        }
    }

}
