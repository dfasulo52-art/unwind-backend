# Usa l'immagine ufficiale Maven con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia tutto il progetto
COPY . .

# Compila l'applicazione usando Maven
RUN mvn clean package -DskipTests

# Fase finale: esecuzione con JRE leggero Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]