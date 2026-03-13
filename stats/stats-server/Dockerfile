FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY . .

RUN mvn -q -pl stats/stats-server -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
COPY --from=build /build/stats/stats-server/target/*.jar /app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]