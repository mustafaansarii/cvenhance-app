# ────────────────── Stage 1: Build ──────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Build common-lib
COPY common-lib/pom.xml ./common-lib/pom.xml
COPY common-lib/src ./common-lib/src
RUN mvn -f common-lib/pom.xml -q -DskipTests install

# Build auth-service
COPY auth-service/pom.xml ./auth-service/pom.xml
RUN mvn -f auth-service/pom.xml dependency:go-offline -q || true
COPY auth-service/src ./auth-service/src
RUN mvn -f auth-service/pom.xml package -DskipTests -q

# Build payment-service
COPY payment-service/pom.xml ./payment-service/pom.xml
RUN mvn -f payment-service/pom.xml dependency:go-offline -q || true
COPY payment-service/src ./payment-service/src
RUN mvn -f payment-service/pom.xml package -DskipTests -q

# Build subscription-service
COPY subscription-service/pom.xml ./subscription-service/pom.xml
RUN mvn -f subscription-service/pom.xml dependency:go-offline -q || true
COPY subscription-service/src ./subscription-service/src
RUN mvn -f subscription-service/pom.xml package -DskipTests -q

# Build doc-service
COPY doc-service/pom.xml ./doc-service/pom.xml
RUN mvn -f doc-service/pom.xml dependency:go-offline -q || true
COPY doc-service/src ./doc-service/src
RUN mvn -f doc-service/pom.xml package -DskipTests -q

# Build ai-agent-service
COPY ai-agent-service/pom.xml ./ai-agent-service/pom.xml
RUN mvn -f ai-agent-service/pom.xml dependency:go-offline -q || true
COPY ai-agent-service/src ./ai-agent-service/src
RUN mvn -f ai-agent-service/pom.xml package -DskipTests -q

# Build api-gateway
COPY api-gateway/pom.xml ./api-gateway/pom.xml
RUN mvn -f api-gateway/pom.xml dependency:go-offline -q || true
COPY api-gateway/src ./api-gateway/src
RUN mvn -f api-gateway/pom.xml package -DskipTests -q

# ────────────────── Stage 2: Runtime ──────────────────
FROM eclipse-temurin:21-jre-jammy

# TeX Live for doc-service LaTeX compilation
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        texlive-latex-base \
        texlive-latex-recommended \
        texlive-latex-extra \
        texlive-fonts-recommended \
        texlive-fonts-extra \
        texlive-science \
        texlive-xetex \
        latexmk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /build/auth-service/target/*.jar auth-service.jar
COPY --from=builder /build/payment-service/target/*.jar payment-service.jar
COPY --from=builder /build/subscription-service/target/*.jar subscription-service.jar
COPY --from=builder /build/doc-service/target/*.jar doc-service.jar
COPY --from=builder /build/ai-agent-service/target/*.jar ai-agent-service.jar
COPY --from=builder /build/api-gateway/target/*.jar api-gateway.jar
COPY start.sh start.sh
RUN chmod +x start.sh

ENV LATEX_COMPILER=native

EXPOSE 8080

ENTRYPOINT ["./start.sh"]
