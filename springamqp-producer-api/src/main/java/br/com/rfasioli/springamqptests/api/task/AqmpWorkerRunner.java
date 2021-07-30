package br.com.rfasioli.springamqptests.api.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.rfasioli.springamqptests.api.producer.confirmreturns.ConfirmReturnsPublisher;
import br.com.rfasioli.springamqptests.api.producer.quorum.quorum.QuorumPublisher;
import lombok.RequiredArgsConstructor;

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
