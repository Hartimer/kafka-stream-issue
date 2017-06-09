# Kafka Streams Issue

This projects reproduces a Kafka Streams issue described under 
[this mailing list thread](https://lists.apache.org/thread.html/863c688860056e16dff9c37eef6596bad9adcf920cbb2a5625668b88@%3Cusers.kafka.apache.org%3E).
  
## Setup

You'll need a local Zookeeper/Kafka

Run the following:

```
$ kafka-topics --zookeeper localhost:2181 --create --partitions 2 --replication-factor 1 --topic demotopic
$ kafka-console-producer --broker-list localhost:9092 --topic demotopic
  sdfsdf
```
## Reproduce

Just run `./gradlew bootRun`

## Notes

All code lives in [StreamDefinition](src/main/java/net/joaopeixoto/streams/StreamDefinition.java).

The exception only happens if we're running 2 threads. IF we only have one we're just stuck in an
infinite rebalancing loop, likely caused by [KAFKA-5073](https://issues.apache.org/jira/browse/KAFKA-5073).
