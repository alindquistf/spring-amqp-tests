package br.com.rfasioli.stream.producer.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.rfasioli.stream.producer.rabbit.RabbitDataProducer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProducerRunner {

    private final RabbitDataProducer producer;

    @Scheduled(fixedDelay = 5)
    public void publish() {
        producer.produce();
    }

}
