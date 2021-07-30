package br.com.rfasioli.springamqptests.api.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rfasioli.springamqptests.api.producer.confirmreturns.ConfirmReturnsPublisher;
import br.com.rfasioli.springamqptests.api.producer.quorum.quorum.QuorumPublisher;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ApiController {
    private final ConfirmReturnsPublisher confirmReturnsPublisher;
    private final QuorumPublisher quorumPublisher;

    @PostMapping("/confirm")
    public void sendConfirmReturns(@RequestBody Integer qty) {
        if (nonNull(qty)) {
            for (int i = 0; i < qty; i++) {
                confirmReturnsPublisher.send();
            }
        }
        else {
            confirmReturnsPublisher.send();
        }
    }

    @PostMapping("/quorum")
    public void sendQuorum(@RequestBody Integer qty) {
        if (nonNull(qty)) {
            for (int i = 0; i < qty; i++) {
                quorumPublisher.send();
            }
        }
        else {
            quorumPublisher.send();
        }
    }

}
