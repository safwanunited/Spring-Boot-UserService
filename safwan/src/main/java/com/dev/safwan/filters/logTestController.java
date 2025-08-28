package com.dev.safwan.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/tes")
public class logTestController {

    private static final Logger log = LoggerFactory.getLogger(logTestController.class);
    private final RestTemplate restTemplate;

    public logTestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        log.info("Handling /api/hello - start");
        log.debug("Doing some internal work for /hello");
        log.info("Handling /api/hello - end");
        return ResponseEntity.ok("Hello — check logs for correlation id");
    }
    @GetMapping("/call")
    public String callingProductService() {
        log.info("Service A: calling service B");
        String res = restTemplate.getForObject("http://localhost:8082/trace/api2", String.class);
        log.info("Service A: received response from B: {}", res);
        return "A->B success (trace=" + MDC.get("traceId") + ")";
    }
}

