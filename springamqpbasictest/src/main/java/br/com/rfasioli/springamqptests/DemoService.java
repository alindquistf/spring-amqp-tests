package br.com.rfasioli.springamqptests;

import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import br.com.rfasioli.springamqptests.quorum.QuorumPublisher;
import br.com.rfasioli.springamqptests.confirmreturns.ConfirmReturnsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class DemoService {
    private final CountDownLatch countDownLatch;
    private final ConfirmReturnsPublisher confirmReturnsPublisher;
    private final QuorumPublisher quorumPublisher;

    @SneakyThrows
    public void run() {
        confirmReturnsPublisher.blockingSend();
        quorumPublisher.send();

        if (countDownLatch.await(10, TimeUnit.SECONDS)) {
            log.info("Messages received by listeners");
        }
        else {
            if (countDownLatch.getCount() == 0L) {
                log.info("Messages NOT received by listener");
            } else {
                log.info("Only {} message(s) received by listener", countDownLatch.getCount());
            }
        }

    }
}
