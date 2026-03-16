FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
COPY stats/stats-server/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app.jar"]