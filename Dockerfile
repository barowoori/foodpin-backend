FROM eclipse-temurin:21-jdk
ENV TZ=Asia/Seoul
ARG OTEL_JAVA_AGENT_VERSION=2.29.0
ARG OTEL_JAVA_AGENT_SHA256=546531ca690a8603d2923b6db26bbda35c6409327b1e610430ae33c2f8f68050
RUN apt-get update && apt-get install -y tzdata curl && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    mkdir -p /opt/opentelemetry && \
    curl --fail --location --silent --show-error "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVA_AGENT_VERSION}/opentelemetry-javaagent.jar" --output /opt/opentelemetry/opentelemetry-javaagent.jar && \
    echo "${OTEL_JAVA_AGENT_SHA256}  /opt/opentelemetry/opentelemetry-javaagent.jar" | sha256sum --check --strict && \
    rm -rf /var/lib/apt/lists/*
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV SPRING_PROFILES_ACTIVE=dev
ENV OTEL_SDK_DISABLED=true
CMD ["sh", "-c", "java -javaagent:/opt/opentelemetry/opentelemetry-javaagent.jar -Xlog:gc*:stdout:time,level,tags -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]
