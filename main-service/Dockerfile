FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
COPY main-service/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/app.jar"]