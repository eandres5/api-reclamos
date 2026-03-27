FROM amazoncorretto:21-alpine
COPY api-reclamos-service/target/api-reclamos-service-1.0.0.war app.war
ENTRYPOINT ["java","-jar","/app.war"]