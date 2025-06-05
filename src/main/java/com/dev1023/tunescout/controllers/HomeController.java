package com.dev1023.tunescout.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "")
@AllArgsConstructor
@Slf4j
public class HomeController {
    @RequestMapping(path = "/")
    public String home() {
        log.info("Received home request");
        return "{\"message\": \"Welcome to the Tunescout API!\"}";
    }

    @RequestMapping(path = "/health")
    public String health() {
        log.info("Received health check request");
        return "{\"version\": \"1.0.0\"}";
    }
}
