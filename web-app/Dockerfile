# Build image
FROM maven:3.8.5-amazoncorretto-17 as builder

COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
WORKDIR /usr/src/app
RUN mvn package
RUN mv target/okta-db-migration-*.jar target/app.jar

# Run image
FROM amazoncorretto:17

COPY --from=builder /usr/src/app/target/app.jar /usr/app/app.jar
WORKDIR /usr/app
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8000