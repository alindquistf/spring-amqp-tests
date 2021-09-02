package br.com.rfasioli.stream.consumer.rabbit;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import br.com.rfasioli.stream.consumer.dto.DataDTO;
import br.com.rfasioli.stream.consumer.repository.Outbox;
import br.com.rfasioli.stream.consumer.repository.OutboxDataService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component("rabbitData")
public class RabbitDataConsumer implements Consumer<Message<DataDTO>> {

    private final OutboxDataService outboxDataService;

    @SneakyThrows
    @Override
    public void accept(final Message<DataDTO> message) {
        final var payload = message.getPayload();
        log.info("Received payload={}", payload);
        outboxDataService.updateConsumerStatus(payload.getId(), Outbox.STATUS.PROCESSED);
    }

}
