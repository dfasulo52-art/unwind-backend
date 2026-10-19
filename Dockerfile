# Usa l'immagine ufficiale Maven con Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia tutto il progetto direttamente
COPY . .

# Compila l'applicazione usando il Maven standard del container
RUN mvn clean package -DskipTests

# Fase finale: esecuzione con JRE leggero
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]