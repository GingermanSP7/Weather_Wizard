FROM amazoncorretto:17.0.8-alpine 

VOLUME [ "/tmp" ]

USER root

RUN apk add curl && apk add busybox-extras
RUN mkdir -p /app/target
RUN mkdir -p ./training
RUN chmod -R 777 /training*

WORKDIR /app/target

COPY ./target/SparkStreamingModule-0.0.1-SNAPSHOT.jar ./SparkStreamingModule-0.0.1-SNAPSHOT.jar

RUN chmod -R 777 /app/*


# ENTRYPOINT [ "java", "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED" ,"-jar", "SparkStreamingModule-0.0.1-SNAPSHOT.jar"]