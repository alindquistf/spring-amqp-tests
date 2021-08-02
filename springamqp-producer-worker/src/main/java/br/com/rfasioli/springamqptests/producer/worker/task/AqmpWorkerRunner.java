package br.com.rfasioli.springamqptests.producer.worker.task;

import br.com.rfasioli.springamqptests.producer.worker.producer.confirmreturns.ConfirmReturnsPublisher;
import br.com.rfasioli.springamqptests.producer.worker.producer.quorum.QuorumPublisher;
import br.com.rfasioli.springamqptests.producer.worker.producer.quorum.QuorumWithConfirmReturnPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AqmpWorkerRunner {

    private final ConfirmReturnsPublisher confirmReturnsPublisher;
    private final QuorumPublisher quorumPublisher;
    private final QuorumWithConfirmReturnPublisher quorumWithConfirmReturnPublisher;

    @Scheduled(fixedDelay = 1)
    public void publishConfirm() {
        confirmReturnsPublisher.send();
    }

    @Scheduled(fixedDelay = 1)
    public void publishQuorum() {
        quorumPublisher.send();
    }

    @Scheduled(fixedDelay = 1)
    public void publishQuorumWithConfirms() {
        quorumWithConfirmReturnPublisher.send();
    }
}
