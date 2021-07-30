package br.com.rfasioli.springamqptests.worker.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboxDataService {
    private final MongoTemplate mongoTemplate;

    public void updateStatus(String correlation, Outbox.STATUS status) {
        final var result = mongoTemplate.updateFirst(
                Query.query(Criteria.where("correlation").is(correlation)),
                Update.update("consumerStatus", status),
                Outbox.class);
        if (!result.wasAcknowledged()) {
            log.error("Comando do update inválido: {}", result);
        }
        if (result.getMatchedCount() != result.getModifiedCount()) {
            log.warn("Não conseguiu atualizar o registro: {}", result);
        }
    }
}
