# ---------- Build Stage ----------
FROM maven:3.9-eclipse-temurin-26 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:26

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java","-jar","app.jar"]