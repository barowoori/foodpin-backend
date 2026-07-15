package com.barowoori.foodpinbackend.config;

import io.opentelemetry.api.trace.Span;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrometheusExemplarConfig {

    @Bean
    public io.prometheus.metrics.tracer.common.SpanContext prometheusSpanContext() {
        return new io.prometheus.metrics.tracer.common.SpanContext() {
            @Override
            public String getCurrentTraceId() {
                return Span.current().getSpanContext().getTraceId();
            }

            @Override
            public String getCurrentSpanId() {
                return Span.current().getSpanContext().getSpanId();
            }

            @Override
            public boolean isCurrentSpanSampled() {
                return Span.current().getSpanContext().isSampled();
            }

            @Override
            public void markCurrentSpanAsExemplar() {
                Span.current().setAttribute(
                    io.prometheus.metrics.tracer.common.SpanContext.EXEMPLAR_ATTRIBUTE_NAME,
                    io.prometheus.metrics.tracer.common.SpanContext.EXEMPLAR_ATTRIBUTE_VALUE
                );
            }
        };
    }
}
