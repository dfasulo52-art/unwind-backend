# Usa un'immagine con Java 17 per compilare ed eseguire l'applicazione
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia i file di configurazione del wrapper e il pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Assicura che il maven wrapper abbia i permessi di esecuzione (Risolve l'exit code 126)
RUN chmod +x mvnw

# Scarica le dipendenze
RUN ./mvnw dependency:go-offline -B

# Copia il resto del codice sorgente e compila
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Fase finale: usa un'immagine JRE leggera per eseguire il file .jar
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]