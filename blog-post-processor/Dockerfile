FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/blog-post-processor*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]