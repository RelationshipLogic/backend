FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S -u 1000 -G spring spring
RUN mkdir -p /app/logs && chown spring:spring /app/logs
COPY --chown=spring:spring build/libs/*.jar app.jar
USER spring:spring
ENTRYPOINT ["java", "-jar", "app.jar"]
