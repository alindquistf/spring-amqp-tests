package br.com.rfasioli.springamqptests.producer.worker.task;

import br.com.rfasioli.springamqptests.producer.worker.producer.confirmreturns.ConfirmReturnsPublisher;
import br.com.rfasioli.springamqptests.producer.worker.producer.quorum.quorum.QuorumPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AqmpWorkerRunner {

    private final ConfirmReturnsPublisher confirmReturnsPublisher;
    private final QuorumPublisher quorumPublisher;

    @Scheduled(fixedDelay = 1)
    public void publishConfirm() {
        confirmReturnsPublisher.send();
    }

    @Scheduled(fixedDelay = 1)
    public void publishQuorum() {
        quorumPublisher.send();
    }
}
