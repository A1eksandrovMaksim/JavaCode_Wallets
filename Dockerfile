FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/wallet-app.jar wallet-app.jar
ENTRYPOINT ["java", "-jar", "/wallet-app.jar"]