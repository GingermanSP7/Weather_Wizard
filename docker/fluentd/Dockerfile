FROM fluentd:v1.16.0-1.0

USER root

RUN apk update && apk upgrade
RUN apk add ruby-dev
RUN apk add curl
RUN apk add busybox-extras
RUN fluent-gem install fluent-plugin-kafka

COPY ./fluent.conf /fluentd/etc/fluent.conf