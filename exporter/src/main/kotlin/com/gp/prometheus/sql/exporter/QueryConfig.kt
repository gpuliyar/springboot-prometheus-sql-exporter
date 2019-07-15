package com.gp.prometheus.sql.exporter

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram
import io.prometheus.client.Summary
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryConfig @Autowired constructor(private val monitoringQueries: List<MonitoringQuery>) : WithLogging() {

    @Bean
    fun gauges(): List<GaugeCollector> =
        monitoringQueries
            .filter { it.type === MonitoringType.GAUGE }
            .map {
                GaugeCollector(Gauge.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels)
                    .register(), it)
            }

    @Bean
    fun counters(): List<CounterCollector> =
        monitoringQueries
            .filter { it.type === MonitoringType.COUNTER }
            .map {
                CounterCollector(Counter.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels)
                    .register(), it)
            }

    @Bean
    fun summaries(): List<SummaryCollector> =
        monitoringQueries
            .filter { it.type === MonitoringType.SUMMARY }
            .map {
                SummaryCollector(Summary.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels)
                    .register(), it)
            }

    @Bean
    fun histograms(): List<HistogramCollector> =
        monitoringQueries
            .filter { it.type === MonitoringType.HISTOGRAM }
            .map {
                HistogramCollector(Histogram.build()
                    .name(it.name)
                    .help(it.description)
                    .labelNames(*it.labels)
                    .register(), it)
            }
}
