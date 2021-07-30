package br.com.rfasioli.springamqptests.quorum;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.com.rfasioli.springamqptests.AmqpConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class QuorumPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void send() {
        this.rabbitTemplate.convertAndSend("", AmqpConfiguration.QUORUM_QUEUE, "foo");
        log.info("Message sent");
    }
}
