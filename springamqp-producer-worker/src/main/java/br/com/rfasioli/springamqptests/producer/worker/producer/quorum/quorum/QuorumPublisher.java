package br.com.rfasioli.springamqptests.producer.worker.producer.quorum.quorum;

import br.com.rfasioli.springamqptests.producer.worker.AmqpConfiguration;
import br.com.rfasioli.springamqptests.producer.worker.producer.dto.DataDTO;
import br.com.rfasioli.springamqptests.producer.worker.repository.Outbox;
import br.com.rfasioli.springamqptests.producer.worker.repository.OutboxDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Log4j2
public class QuorumPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final AtomicLong counter = new AtomicLong();
    private final OutboxDataService outboxDataService;

    @Async
    public void send() {
        final var correlationData = new CorrelationData(UUID.randomUUID().toString());
        final var message = buildMessage(correlationData.getId());

        outboxDataService.save(
                Outbox.of(null, correlationData.getId(), message, Outbox.STATUS.PENDING, null, Outbox.TYPE.QUORUM));

        try {
            this.rabbitTemplate.convertAndSend(AmqpConfiguration.QUORUM_EXCHANGE, AmqpConfiguration.QUORUM_QUEUE, message);
            log.info("Message sent");
            outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.SENT);
        } catch (Exception e) {
            log.error("Erro enviando a mensagem: {}", e.getMessage());
            outboxDataService.updateStatus(correlationData.getId(), Outbox.STATUS.ERROR);
        }
    }

    @SneakyThrows
    private String buildMessage(String correlation) {
        return objectMapper.writeValueAsString(
                DataDTO.of(
                        correlation,
                        counter.addAndGet(1)
                ));
    }

}
