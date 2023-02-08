FROM adoptopenjdk/openjdk11
ARG JAR_FILE=target/final-capstone-starter-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} final-capstone-starter-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/final-capstone-starter-0.0.1-SNAPSHOT.jar"]