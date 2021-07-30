package br.com.rfasioli.springamqptests.confirmreturns;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import br.com.rfasioli.springamqptests.AmqpConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class ConfirmReturnsPublisher {

    private final RabbitTemplate rabbitTemplate;

    @SneakyThrows
    public void blockingSend() {
        final var correlationData = new CorrelationData("1");
        this.rabbitTemplate.convertAndSend("", AmqpConfiguration.QUEUE, "foo", correlationData);
        final var confirm = correlationData.getFuture().get(10, TimeUnit.SECONDS);
        log.info("Confirm received, ack = {}", confirm.isAck());
    }

}
