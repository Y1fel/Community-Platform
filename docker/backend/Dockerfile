FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY backend-system-community/pom.xml ./pom.xml

COPY backend-system-community/src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre

RUN useradd --system --uid 10001 --create-home appuser
WORKDIR /app

COPY --from=build /workspace/target/community-backend-*.jar /app/community-backend.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/community-backend.jar"]

