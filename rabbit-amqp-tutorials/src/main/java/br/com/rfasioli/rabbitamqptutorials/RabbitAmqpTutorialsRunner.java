package br.com.rfasioli.rabbitamqptutorials;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class RabbitAmqpTutorialsRunner implements CommandLineRunner {

  @Value("${tutorial.client.duration:0}")
  private int duration;

  private final ConfigurableApplicationContext ctx;

  @Override
  public void run(String... arg0) throws Exception {
    log.info("Ready ... running for {} ms",  duration);
    Thread.sleep(duration);
    ctx.close();
  }
}
