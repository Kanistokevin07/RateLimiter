package com.ratelimiter.config;

import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;


public class MetricsConfig {

    private final PrometheusMeterRegistry registry;


    public MetricsConfig() {

        registry =
                new PrometheusMeterRegistry(
                        PrometheusConfig.DEFAULT
                );


        // JVM metrics

        new ClassLoaderMetrics()
                .bindTo(registry);


        new JvmMemoryMetrics()
                .bindTo(registry);


        new JvmThreadMetrics()
                .bindTo(registry);


        new ProcessorMetrics()
                .bindTo(registry);


        new UptimeMetrics()
                .bindTo(registry);

    }


    public PrometheusMeterRegistry registry() {

        return registry;

    }

}