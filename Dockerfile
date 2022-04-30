FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM openjdk:17
EXPOSE 8080:8080
RUN mkdir /app
WORKDIR /home/gradle/src
COPY --from=build /home/gradle/src/build/libs/*.jar /app/muon.jar
#COPY build/libs/*-all.jar /app/muon.jar
ENTRYPOINT ["java","-jar","/app/muon.jar"]