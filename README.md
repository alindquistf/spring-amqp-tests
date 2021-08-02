# Spring AMQP Tests
Este projeto tem a finalidade de validar a resiliência das classic e quorum queues do RabbitMQ em
cenários de CAOS. Derrubando instâncias isoladamente ou agrupadas, como também derrubando as 
aplicações de maneira aleatória.

Para execução dos testes foi criado um docker-compose que disponibiliza quatro instâncias 
clusterizadas do RabbitMQ e uma instância do MongoDB, para registro dos dados.

<b>Foram simulados os cenários:</b>

- Derrubar cada uma das instâncias do RabbitMQ isoladamente;
- Derrubar as instâncias do RabbitMQ em pares;
- Derrubar 3 instâncias do RabbitMQ simultaneamente;
- Derrubar todas as instâncias do RabbitMQ simultaneamente;
- Derrubar a instância "Leader" da publicação do RabbitMQ;
- Derrubar a aplicação consumidora;
- Derrubar a aplicação produtora;
- Combinação de cenárias derrubando aplicações e instâncias do rabbitMQ

## Requisitos
- Java 11
- Docker
- Docker Compose

## Executação
### Containeres
``` bash
# subir instâncias
docker-compose up -d

# baixar instâncias do RabbitMQ
docker stop springamqptests_rabbit4_1 springamqptests_rabbit3_1 springamqptests_rabbit2_1 springamqptests_rabbit1_1
```
><i><b>obs:</b> <b><u>NÃO</u></b> baixar as instâncias pelo `docker-compose down`, pois irá baixar o MongoDB
e as aplicações não estão preparadas para este cenário.</i>

#### Conectar no MongoDB
Para conectar no banco de dados, basta usar qualquer client conectando na url: `mongodb://localhost:27017/test`  

#### Painel do RabbitMQ
Basta acessar a url <i> http://localhost:15672 </i>, com usuário `admin` e senha `admin`. 

### Aplicações
Podem ser iniciadas pela própria IDE ou pelo terminal, sem parâmetros adicionais.
``` bash
# Serviço Rest
./gradlew springamqp-producer-api:bootRun

# Worker, produz mensagens para as 3 exchanges a cada 1 milisegundo. 
./gradlew springamqp-producer-worker:bootRun

# Consumer, processa quatro threads para cada fila.
./gradlew springamqp-consumer-worker:bootRun
```

## Avaliando resultados
Para verificar as mensagens em situação irregular, precisamos listar os registros de mensagens não
processadas em situações diferentes de erro(ERROR) ou não aceita (NACK). Para isso execute a 
seguinte query no MongoDB:
``` json
db.outbox.find({
  status: { $nin: [ 'ERROR', 'NACK' ] }, 
  consumerStatus: { $ne: 'PROCESSED' } 
})
```

- As mensagens em situação ERROR e NACK devem ser tratadas pela aplicação produtora, ou no caso do 
  padrão <b>Outbox</b>, pelo job outbox. Reportando o erro ou repostando a mensagem.

- Mensagens com <b>status</b> `SENT` ou `PENDING`, sem um <b>consumerStatus</b> e com as filas sem 
  mensagens pendentes, significam que houve erro em sua postagem.

## Resultados obtidos
1) Os cenários de queda das aplicações não influenciaram na perda de mensagens.
2) Para os cenário derrubando apenas 1 ou 2 servidores do cluster RabbitMQ, não houve perda de 
   mensagens.
3) Para os cenário derrubando 3 servidores do cluster RabbitMQ, começamos a ter perda de mensagens 
   para as <i>"Quorum Queues"</i> sem o tratamento de <i>"confirm returns"</i>.
4) Derrubando todo o cluster do RabbitMQ a perda de mensagens se intensificou para as
   <i>"Quorum Queues"</i> sem o tratamento de <i>"confirm returns"</i>.
5) Derrubando a instância "Leader" da publicação, para a fila <i>"Classic"</i> com 
   <i>"confirm returns</i>, a mensagem não foi consumida enquanto a instância não se recuperou.

### Recomendação:
Para cenários onde a garantia de entrega da mensagem é prioritária e as Quorum queues não gerem 
restrições, a recomendação é o uso destas associada ao confirm returns.
