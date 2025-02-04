FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/blog-fetcher*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]