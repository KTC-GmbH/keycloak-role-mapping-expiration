FROM quay.io/keycloak/keycloak:18.0.2 AS builder

ENV KC_METRICS_ENABLED=true
ENV KC_HEALTH_ENABLED=true
ENV KC_HTTP_RELATIVE_PATH=/auth
ENV KC_DB=postgres

COPY /target/keycloak-service-provider.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:18.0.2

ENV KEYCLOAK_ADMIN=${KEYCLOAK_USER:-"user"}
ENV KEYCLOAK_ADMIN_PASSWORD=${KEYCLOAK_PASSWORD:-"password123"}
ENV KC_DB_URL_HOST=${DB_ADDR:-"localhost"}
ENV KC_DB_URL_DATABASE=${DB_DATABASE:-"postgres"}
ENV KC_DB_USERNAME=${DB_USER:-"postgres"}
ENV KC_DB_PASSWORD=${DB_PASSWORD:-"postgres"}
ENV KC_HTTP_RELATIVE_PATH="/auth"
ENV KC_HOSTNAME_STRICT=false
ENV KC_HTTP_ENABLED=true
ENV KC_PROXY="edge"
ENV KC_DB=postgres

COPY --from=builder /opt/keycloak/providers/ /opt/keycloak/providers/
COPY --from=builder /opt/keycloak/lib/quarkus/ /opt/keycloak/lib/quarkus/

ENTRYPOINT /opt/keycloak/bin/kc.sh start-dev