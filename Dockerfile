FROM maven:3-openjdk-17 as build
COPY . /app
WORKDIR /app
RUN mvn -f pom.xml clean package -DskipTests

FROM openjdk:17-jdk-oracle
EXPOSE 8080
COPY --from=build /app/target/backend-igdb-proxy-0.0.1-SNAPSHOT.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","app.jar"]