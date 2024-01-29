FROM amazoncorretto:17.0.8-alpine 

VOLUME [ "/tmp" ]

USER root
RUN apk add curl && apk add busybox-extras
RUN mkdir -p /app/target
WORKDIR /app/target
COPY ./target/KafkaStreamModule-0.0.1-SNAPSHOT.jar ./KafkaStreamModule-0.0.1-SNAPSHOT.jar
RUN chmod 777 /app/*

ENTRYPOINT [ "java", "-jar", "KafkaStreamModule-0.0.1-SNAPSHOT.jar" ]