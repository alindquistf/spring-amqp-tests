package br.com.rfasioli.springamqptests.confirmreturns;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

import br.com.rfasioli.springamqptests.AmqpConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class ConfirmReturnsListener {

    private final CountDownLatch countDownLatch;

    @RabbitListener(queues = AmqpConfiguration.QUEUE)
    public void listen(String in) {
        log.info("Listener received: {}", in);
        this.countDownLatch.countDown();
    }
}
