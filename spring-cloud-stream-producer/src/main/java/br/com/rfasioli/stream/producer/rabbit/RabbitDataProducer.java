package br.com.rfasioli.stream.producer.rabbit;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import br.com.rfasioli.stream.producer.dto.DataDTO;
import br.com.rfasioli.stream.producer.repository.Outbox;
import br.com.rfasioli.stream.producer.repository.OutboxDataService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Log4j2
@RequiredArgsConstructor
@Component("rabbitData")
public class RabbitDataProducer implements Supplier<Flux<Message<DataDTO>>> {

    private final Sinks.Many<Message<DataDTO>> sink = Sinks.many().unicast().onBackpressureBuffer();

    private final AtomicLong counter = new AtomicLong();

    private final OutboxDataService outboxDataService;

    @SneakyThrows
    public void produce() {
        final var correlationData = new CorrelationData(UUID.randomUUID().toString());
        final var payload = DataDTO.of(correlationData.getId(), counter.addAndGet(1));

        outboxDataService.save(Outbox.of(
                null,
                correlationData.getId(),
                payload.toString(),
                Outbox.STATUS.PENDING,
                null,
                Outbox.TYPE.CLOUD_STREAM));

        sink.emitNext(MessageBuilder.withPayload(payload)
                        .setHeader(AmqpHeaders.PUBLISH_CONFIRM_CORRELATION, correlationData)
                        .build(),
                Sinks.EmitFailureHandler.FAIL_FAST);
        log.info("Produced payload={}", payload);

        final var confirmFuture = correlationData.getFuture();
        confirmFuture.addCallback(publishConfirmsCallback(correlationData));
    }

    public ListenableFutureCallback<CorrelationData.Confirm> publishConfirmsCallback(final CorrelationData correlationData) {
        return new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(final CorrelationData.Confirm result) {
                final var ack = result.isAck();
                final var returned = correlationData.getReturned() != null;
                log.info("Publish confirms: correlationId={}, ack={}, returned={}", correlationData.getId(), ack, returned);
                if (!returned && ack) {
                    outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.SENT);
                } else {
                    outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.NACK);
                }
            }

            @Override
            public void onFailure(final Throwable ex) {
                log.error("Error while waiting confirmation for correlationId={}", correlationData.getId(), ex);
                outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.ERROR);
            }
        };
    }

    @Override
    public Flux<Message<DataDTO>> get() {
        return sink.asFlux();
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
