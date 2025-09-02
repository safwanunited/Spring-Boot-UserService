package com.dev.safwan.filters;

import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/trace")
public class logTestController {

    private static final Logger log = LoggerFactory.getLogger(logTestController.class);

    private final RestTemplateBuilder restTemplateBuilder;
    private final Tracer tracer;

    public logTestController(RestTemplateBuilder restTemplateBuilder, Tracer tracer) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.tracer = tracer;
    }

    @GetMapping("/internal")
    public ResponseEntity<String> hello() {
        var span = tracer.currentSpan();
        String traceId = span != null ? span.context().traceId() : "none";
        String spanId = span != null ? span.context().spanId() : "none";

        log.info("Handling /trace/internal - traceId={}, spanId={}", traceId, spanId);
        log.debug("Doing some internal work for /trace/internal");
        log.info("Handling /trace/internal - end");

        return ResponseEntity.ok("Hello — traceId=" + traceId + ", spanId=" + spanId);
    }

    @GetMapping("/external")
    public String callingProductService() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        log.info("Service A: calling service B");
        String res = restTemplate.getForObject("http://localhost:8082/trace/api2", String.class);
        log.info("Service A: received response from B: {}", res);

        return "A->B success (trace=" + MDC.get("traceId") + ", span=" + MDC.get("spanId") + ")";
    }
}

