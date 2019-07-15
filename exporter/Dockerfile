# ***************** Stage 1 *****************
FROM maven:3-jdk-8-alpine as builder

WORKDIR /exporter-src

COPY . /exporter-src

RUN mvn package

# ***************** Stage 2 *****************
FROM openjdk:8-jre-alpine3.9

ENV EXPORTER_HOME /opt/prometheus-sql-exporter

WORKDIR ${EXPORTER_HOME}

COPY --from=builder /exporter-src/target/prometheus.sql.exporter-1.0.0.jar exporter.jar
COPY --from=builder /exporter-src/config/* config/
COPY --from=builder /exporter-src/queries/* queries/

RUN set -ex; \
    \
    addgroup -S -g 1000 exporter; \
    adduser -S -D -s /sbin/nologin -G exporter -u 1000 exporter; \
    \
    chown -R exporter:exporter ${EXPORTER_HOME}; \
    chown -R exporter:exporter ${JAVA_HOME};

EXPOSE 8080

USER exporter

ENTRYPOINT [ "java", "-jar", "exporter.jar" ]
