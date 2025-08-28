package com.dev.safwan.filters;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Configuration
public class TracingConfig {
    private static final String TRACE_HEADER       = "X-Correlation-ID";
    private static final String SPAN_HEADER        = "X-Span-ID";
    private static final String PARENT_SPAN_HEADER = "X-Parent-Span-ID";

    private static final String TRACE_MDC_KEY      = "traceId";
    private static final String SPAN_MDC_KEY       = "spanId";
    private static final String OUTGOING_MDC_KEY   = "outgoingSpanId"; // optional

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rt = new RestTemplate();
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            String traceId = MDC.get(TRACE_MDC_KEY);

            String parentSpan = MDC.get(SPAN_MDC_KEY);

            String outgoingSpan = UUID.randomUUID().toString();

            request.getHeaders().add(TRACE_HEADER, traceId);
            request.getHeaders().add(SPAN_HEADER, outgoingSpan);
            if (parentSpan != null && !parentSpan.isBlank()) {
                request.getHeaders().add(PARENT_SPAN_HEADER, parentSpan);
            }

            MDC.put(OUTGOING_MDC_KEY, outgoingSpan);
            try {
                return execution.execute(request, body);
            } finally {
                MDC.remove(OUTGOING_MDC_KEY);
            }
        };
        rt.setInterceptors(List.of(interceptor));
        return rt;
    }
}
