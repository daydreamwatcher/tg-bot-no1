FROM openjdk:17

COPY ./target/spring-tg-bot-0.0.1.jar .

ENTRYPOINT ["java","-jar","spring-tg-bot-0.0.1.jar"]
