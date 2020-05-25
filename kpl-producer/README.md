# Kinesis Producers

## KPL Producer
1. Producer created using KPL libraries. 
### Getting Started

```
//Build 
mvn clean install -DskipTests
// Deploy and start consumer
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DshardCount=<shard-count> -Dserver.port=<port>"
//Shutdown consumer
curl -X post http://localhost:<port>/actuator/shutdown

```
