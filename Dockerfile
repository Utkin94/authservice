FROM openjdk:11-slim

WORKDIR ./home/app

COPY ./target/authService-0.0.1-SNAPSHOT.jar ./

EXPOSE 8080

CMD java -jar authService-0.0.1-SNAPSHOT.jar