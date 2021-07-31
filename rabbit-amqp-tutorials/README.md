# Tutorials RabbitMQ - Spring AMQP
Link: [https://www.rabbitmq.com/getstarted.html](https://www.rabbitmq.com/getstarted.html)

## Compilando
``` bash
./gradlew bootJar
```

## Executando
### Hello World
``` bash
# consumer
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=hello-world,receiver

# sender
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=hello-world,sender
```

### Hello World
``` bash
# consumer
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=work-queues,receiver

# sender
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=work-queues,sender
```

### Pub Sub
``` bash
# consumer
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=pub-sub,receiver --tutorial.client.duration=60000

# sender
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=pub-sub,sender --tutorial.client.duration=60000
```

### Routing
``` bash
# consumer
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=routing,receiver --tutorial.client.duration=60000

# sender
java -jar build/libs/rabbit-amqp-tutorials-0.0.1-SNAPSHOT.jar --spring.profiles.active=routing,receiver --tutorial.client.duration=60000
```
