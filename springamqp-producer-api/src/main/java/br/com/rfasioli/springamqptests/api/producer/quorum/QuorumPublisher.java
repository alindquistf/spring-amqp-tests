package br.com.rfasioli.springamqptests.api.producer.quorum;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import br.com.rfasioli.springamqptests.api.AmqpConfiguration;
import br.com.rfasioli.springamqptests.api.producer.dto.DataDTO;
import br.com.rfasioli.springamqptests.api.repository.Outbox;
import br.com.rfasioli.springamqptests.api.repository.OutboxDataService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

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
