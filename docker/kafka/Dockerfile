FROM amazoncorretto:17
ENV PATH /opt/kafka/bin:$PATH
ENV KAFKA_DIR "/opt/kafka"
ARG KAFKA_VERSION="2.13-3.4.0"

RUN yum install -y wget
ADD setup/kafka_${KAFKA_VERSION}.tgz /opt

RUN ln -s /opt/kafka_${KAFKA_VERSION} ${KAFKA_DIR} 

ADD config/* ${KAFKA_DIR}/config/
COPY ./ww_create_topics.sh /opt/kafka_2.13-3.4.0/bin
RUN chmod -R 777 /opt/*