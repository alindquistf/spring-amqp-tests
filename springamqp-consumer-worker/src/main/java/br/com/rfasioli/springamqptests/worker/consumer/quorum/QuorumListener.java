package br.com.rfasioli.springamqptests.worker.consumer.quorum;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.com.rfasioli.springamqptests.worker.AmqpConfiguration;
import br.com.rfasioli.springamqptests.worker.consumer.dto.DataDTO;
import br.com.rfasioli.springamqptests.worker.repository.Outbox;
import br.com.rfasioli.springamqptests.worker.repository.OutboxDataService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class QuorumListener {

    private final ObjectMapper objectMapper;
    private final OutboxDataService outboxDataService;

    @SneakyThrows
    @RabbitListener(queues = AmqpConfiguration.QUORUM_QUEUE, concurrency = "4-4")
    public void listen(final String in) {
        log.info("Listener received: {}", in);
        final var received = objectMapper.readValue(in, DataDTO.class);
        outboxDataService.updateStatus(received.getId(), Outbox.STATUS.PROCESSED);
        Thread.sleep(5);
    }
}
