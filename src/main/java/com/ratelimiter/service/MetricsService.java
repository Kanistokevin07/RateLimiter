package com.ratelimiter.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;


public class MetricsService {

    private final Counter totalRequests;

    private final Counter allowedRequests;

    private final Counter rejectedRequests;

    private final Timer requestLatency;


    public MetricsService(PrometheusMeterRegistry registry) {


        totalRequests =
                Counter.builder("rate_limit_requests_total")
                        .description("Total rate limit requests")
                        .register(registry);


        allowedRequests =
                Counter.builder("rate_limit_allowed_total")
                        .description("Total allowed requests")
                        .register(registry);


        rejectedRequests =
                Counter.builder("rate_limit_rejected_total")
                        .description("Total rejected requests")
                        .register(registry);


        requestLatency =
                Timer.builder("rate_limit_latency_seconds")
                        .description("Rate limiter request latency")
                        .publishPercentileHistogram()
                        .register(registry);

    }


    public void incrementTotalRequests() {

        totalRequests.increment();

    }


    public void incrementAllowedRequests() {

        allowedRequests.increment();

    }


    public void incrementRejectedRequests() {

        rejectedRequests.increment();

    }


    public Timer getRequestLatency() {

        return requestLatency;

    }

}