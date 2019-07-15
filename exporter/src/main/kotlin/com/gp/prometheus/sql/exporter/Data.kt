package com.gp.prometheus.sql.exporter

import com.fasterxml.jackson.annotation.JsonProperty
import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram
import io.prometheus.client.Summary

data class MonitoringQuery(
    val type: MonitoringType,
    val name: String,
    val description: String,
    val labels: Array<String>,
    val sql: String,
    val valueColumn: String = "amount"
)

interface PrometheusCollector {
    val monitoringQuery: MonitoringQuery
}

data class GaugeCollector(
    val gauge: Gauge,
    override val monitoringQuery: MonitoringQuery
) : PrometheusCollector

data class CounterCollector(
    val counter: Counter,
    override val monitoringQuery: MonitoringQuery
) : PrometheusCollector

data class SummaryCollector(
    val summary: Summary,
    override val monitoringQuery: MonitoringQuery
) : PrometheusCollector

data class HistogramCollector(
    val histogram: Histogram,
    override val monitoringQuery: MonitoringQuery
) : PrometheusCollector

data class QueryResult(
    val labelValues: Array<String>,
    val value: Double
)

enum class MonitoringType {
    @JsonProperty("gauge")
    GAUGE,
    @JsonProperty("counter")
    COUNTER,
    @JsonProperty("summary")
    SUMMARY,
    @JsonProperty("histogram")
    HISTOGRAM
}
