package br.com.rfasioli.rabbitamqptutorials.tut1;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@RabbitListener(queues = "hello")
@Log4j2
public class Tut1Receiver {
  @RabbitHandler
  public void receive(String in) {
    log.info(" [x] Received '{}'", in);
  }
}
