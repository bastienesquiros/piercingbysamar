# ── Stage 1 : Build ───────────────────────────────────────────
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copie le wrapper Maven en premier pour profiter du cache Docker
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q

# Copie le code source et build
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ── Stage 2 : Runtime ─────────────────────────────────────────
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# Utilisateur non-root pour la sécurité
RUN groupadd -r samar && useradd -r -g samar samar

COPY --from=build /app/target/*.jar app.jar
RUN chown samar:samar app.jar

USER samar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
