package com.gp.prometheus.sql.exporter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.prometheus.client.Collector
import io.prometheus.client.exporter.MetricsServlet
import io.prometheus.client.hotspot.DefaultExports
import io.prometheus.client.spring.boot.SpringBootMetricsCollector
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.endpoint.PublicMetrics
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import java.io.File
import java.util.concurrent.Executors

@SpringBootApplication
class App : WithLogging() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(App::class.java, *args)
        }
    }

    @Bean
    fun springBootMetricsCollector(publicMetrics: Collection<PublicMetrics>): SpringBootMetricsCollector {
        val springBootMetricsCollector = SpringBootMetricsCollector(publicMetrics)
        springBootMetricsCollector.register<Collector>()
        return springBootMetricsCollector
    }

    @Bean
    fun servletRegistrationBean(): ServletRegistrationBean {
        DefaultExports.initialize()
        return ServletRegistrationBean(MetricsServlet(), "/metrics")
    }

    @Bean
    fun monitoringQueries(): List<MonitoringQuery> {
        val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

        return try {
            File("./queries/").listFiles { f -> f.isFile }
                .map { mapper.readValue<MonitoringQuery>(it) }
        } catch (exception: Exception) {
            log.error("Failed to parse query files.", exception)
            emptyList()
        }
    }

    @Bean
    fun taskScheduler(): TaskScheduler {
        return ConcurrentTaskScheduler(Executors.newScheduledThreadPool(5))
    }
}
