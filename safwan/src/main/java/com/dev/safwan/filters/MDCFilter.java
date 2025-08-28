package com.dev.safwan.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MDCFilter.class);

    private static final String TRACE_HEADER       = "X-Correlation-ID";
    private static final String SPAN_HEADER        = "X-Span-ID";
    private static final String PARENT_SPAN_HEADER = "X-Parent-Span-ID";

    private static final String TRACE_MDC_KEY = "traceId";
    private static final String SPAN_MDC_KEY  = "spanId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.debug("MdcFilter entered: incoming trace={}, incoming span={}",
                request.getHeader(TRACE_HEADER), request.getHeader(SPAN_HEADER));

        String traceId = request.getHeader(TRACE_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        String incomingSpan = request.getHeader(SPAN_HEADER);
        if (incomingSpan != null && incomingSpan.isBlank()) {
            incomingSpan = null;
        }

        String currentSpan = UUID.randomUUID().toString();

        MDC.put(TRACE_MDC_KEY, traceId);
        MDC.put(SPAN_MDC_KEY, currentSpan);

        response.setHeader(TRACE_HEADER, traceId);
        response.setHeader(SPAN_HEADER, currentSpan);
        if (incomingSpan != null) {
            response.setHeader(PARENT_SPAN_HEADER, incomingSpan);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_MDC_KEY);
            MDC.remove(SPAN_MDC_KEY);
            log.debug("MdcFilter exiting: cleared MDC for trace={}, span={}", traceId, currentSpan);
        }
    }
}
