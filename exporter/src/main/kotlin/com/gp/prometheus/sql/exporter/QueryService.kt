package com.gp.prometheus.sql.exporter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class QueryService @Autowired constructor(private val jdbcTemplate: JdbcTemplate,
                                          private val gauges: List<GaugeCollector>,
                                          private val counters: List<CounterCollector>,
                                          private val summaries: List<SummaryCollector>,
                                          private val histograms: List<HistogramCollector>) : WithLogging() {

    @Scheduled(fixedRateString = "\${query.interval.gauge}")
    fun queryGauge() {
        gauges.forEach(runQuery { query, result ->
            query.gauge.labels(*result.labelValues).inc(result.value)
        })
    }

    @Scheduled(fixedRateString = "\${query.interval.counter}")
    fun queryCounter() {
        counters.forEach(runQuery { query, result ->
            query.counter.labels(*result.labelValues).inc(result.value)
        })
    }

    @Scheduled(fixedRateString = "\${query.interval.summary}")
    fun querySummary() {
        summaries.forEach(runQuery { query, result ->
            query.summary.labels(*result.labelValues).observe(result.value)
        })
    }

    @Scheduled(fixedRateString = "\${query.interval.histogram}")
    fun queryHistogram() {
        histograms.forEach(runQuery { query, result ->
            query.histogram.labels(*result.labelValues).observe(result.value)
        })
    }

    private fun <T : PrometheusCollector> runQuery(populateMetric: (T, QueryResult) -> Unit): (T) -> Unit {
        return { collector ->
            log.info("Running Query: {}", collector.monitoringQuery)
            jdbcTemplate.query(collector.monitoringQuery.sql, mapRow(collector)).forEach { result ->
                populateMetric(collector, result)
            }
        }
    }

    private fun mapRow(collector: PrometheusCollector): RowMapper<QueryResult> {
        val query = collector.monitoringQuery
        return RowMapper { resultSet, _ ->
            val labelValues = query.labels
                .map { column ->
                    try {
                        resultSet.getString(column)
                    } catch (sqlException: SQLException) {
                        log.error("Failed to get value for column {} from query", column, sqlException)
                        ""
                    }
                }.toTypedArray()
            QueryResult(labelValues, resultSet.getDouble(query.valueColumn))
        }
    }
}
