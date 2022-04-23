FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} timer.jar
ENTRYPOINT ["java","-jar","/timer.jar"]
EXPOSE 8080
