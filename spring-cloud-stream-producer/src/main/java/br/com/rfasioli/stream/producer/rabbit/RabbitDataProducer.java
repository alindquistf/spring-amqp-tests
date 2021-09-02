package br.com.rfasioli.stream.producer.rabbit;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
    @Async
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

        try {
            final var confirm = correlationData.getFuture().get(10, TimeUnit.SECONDS);
            log.info("Confirm received: correlationId={} ack={}", correlationData.getId(), confirm.isAck());

            if (confirm.isAck()) {
                outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.SENT);
            } else {
                outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.NACK);
            }
        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error while waiting confirmation for correlationId={}", correlationData.getId(), e);
            outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.ERROR);
        }
    }

    @Override
    public Flux<Message<DataDTO>> get() {
        return sink.asFlux();
    }

}
