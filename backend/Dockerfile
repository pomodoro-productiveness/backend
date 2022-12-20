FROM amazoncorretto:17.0.5
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} backend.jar
COPY credentials.json credentials.json
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/backend.jar"]
EXPOSE 8080
