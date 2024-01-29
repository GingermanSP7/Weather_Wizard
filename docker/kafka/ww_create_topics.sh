#!/bin/bash

if [ -d "$directory"]; then
    ./kafka-topics.sh --create --topic 'Catania' --partitions 2 --replication-factor 1 --bootstrap-server localhost:9092
    ./kafka-topics.sh --create --topic 'Milano' --partitions 2 --replication-factor 1 --bootstrap-server localhost:9092
    ./kafka-topics.sh --create --topic 'Roma' --partitions 2 --replication-factor 1 --bootstrap-server localhost:9092

    ./kafka-topics.sh --create --topic 'WeatherCT' --partitions 2 --replication-factor 1 --bootstrap-server localhost:9092
    ./kafka-topics.sh --create --topic 'WeatherMI' --partitions 2 --replication-factor 1 --bootstrap-server localhost:9092
    ./kafka-topics.sh --create --topic 'WeatherRM' --partitions 2 --replication-factor 1 --bootstrap-server localhost:9092

    echo "Topics created successfully!"
    exit
else 
    echo "Error during creating topics"
fi