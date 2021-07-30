package br.com.rfasioli.springamqptests;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Configuration
@RequiredArgsConstructor
@Log4j2
public class AmqpConfiguration {

    public static final String QUEUE = "ha_ac_exchange_notify_createtransaction_tecban";
    public static final String QUORUM_QUEUE = "ha_ac_quorumqueue_notify_createtransaction_tecban";

    private final RabbitTemplate rabbitTemplate;
    private final SetupCallbacks setupCallbacks;

    @Bean
    public CountDownLatch countDownLatch() {
        return new CountDownLatch(2);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue quorumqueue() {
        return new Queue(QUORUM_QUEUE, true, false, false,
                new StringObjectMapBuilder()
                        .put("x-queue-type", "quorum")
                        .get());
    }

    @Component
    private static class SetupCallbacks {
        public SetupCallbacks(final RabbitTemplate rabbitTemplate) {
            rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
                if (correlation != null) {
                    log.info("Received {} for correlation: {}, reason: {}",
                            (ack ? " ack " : " nack "), correlation, reason);
                }
            });
        }
    }

}
