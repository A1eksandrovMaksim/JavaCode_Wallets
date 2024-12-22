FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/JavaCodeWallets-1.0-SNAPSHOT.jar wallet-app.jar
ENTRYPOINT ["java", "-jar", "wallet-app.jar"]