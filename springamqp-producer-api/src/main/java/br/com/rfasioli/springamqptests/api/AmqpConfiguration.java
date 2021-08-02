package br.com.rfasioli.springamqptests.api;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.StringObjectMapBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class AmqpConfiguration {

    public static final String EXCHANGE = "ha_ac_exchange_notify_createtransaction_tecban";
    public static final String QUORUM_EXCHANGE = "ha_ac_quorumexchange_notify_createtransaction_tecban";
    public static final String QUORUM_CONFIRMS_EXCHANGE = "ha_ac_quorum_confirm_exchange_notify_createtransaction_tecban";

    public static final String QUEUE = "ha_ac_queue_notify_createtransaction_tecban";
    public static final String QUORUM_QUEUE = "ha_ac_quorumqueue_notify_createtransaction_tecban";
    public static final String QUORUM_CONFIRMS_QUEUE = "ha_ac_quorum_confirm_queue_notify_createtransaction_tecban";

    private final RabbitTemplate rabbitTemplate;
    private final SetupCallbacks setupCallbacks;

    @Bean("exchange")
    public FanoutExchange exchange() {
        final var exchange = new FanoutExchange(EXCHANGE);
        exchange.setDelayed(false);
        return exchange;
    }

    @Bean("quorumexchange")
    public FanoutExchange quorumexchange() {
        final var exchange = new FanoutExchange(QUORUM_EXCHANGE);
        exchange.setDelayed(false);
        return exchange;
    }

    @Bean("quorumConfirmsExchange")
    public DirectExchange quorumConfirmsExchange() {
        final var exchange = new DirectExchange(QUORUM_CONFIRMS_EXCHANGE);
        exchange.setDelayed(false);
        return exchange;
    }

    @Bean("queue")
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean("quorumqueue")
    public Queue quorumqueue() {
        return new Queue(QUORUM_QUEUE, true, false, false,
                new StringObjectMapBuilder()
                        .put("x-queue-type", "quorum")
                        .get());
    }

    @Bean("quorumConfirmsQueue")
    public Queue quorumConfirmsQueue() {
        return new Queue(QUORUM_CONFIRMS_QUEUE, true, false, false,
                new StringObjectMapBuilder()
                        .put("x-queue-type", "quorum")
                        .get());
    }

    @Bean("bindingQueue")
    public Binding bindingQueue(final Queue queue,
                                final FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean("bindingQuorumQueue")
    public Binding bindingQuorumQueue(@Qualifier("quorumqueue") final Queue quorumqueue,
                                      @Qualifier("quorumexchange") final FanoutExchange quorumexchange) {
        return BindingBuilder.bind(quorumqueue).to(quorumexchange);
    }

    @Bean("bindingQuorumConfirmsQueue")
    public Binding bindingQuorumConfirmsQueue(@Qualifier("quorumConfirmsQueue") final Queue quorumConfirmsQueue,
                                              @Qualifier("quorumConfirmsExchange") final FanoutExchange quorumConfirmsExchange) {
        return BindingBuilder.bind(quorumConfirmsQueue).to(quorumConfirmsExchange);
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
