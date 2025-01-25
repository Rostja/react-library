FROM openjdk:17
ADD target/spring-boot-library.jar spring-boot-library.jar
ENTRYPOINT ["java","-jar","/spring-boot-library.jar"]