package com.ratelimiter.controller;

import io.javalin.Javalin;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class MetricsController {

    private final PrometheusMeterRegistry registry;

    public MetricsController(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    public void registerRoutes(Javalin app) {

        app.get("/metrics", ctx -> {

            ctx.contentType("text/plain");

            ctx.result(
                    registry.scrape()
            );
        });
    }
}