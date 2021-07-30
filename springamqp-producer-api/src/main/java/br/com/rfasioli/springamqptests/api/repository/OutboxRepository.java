package br.com.rfasioli.springamqptests.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OutboxRepository extends MongoRepository<Outbox, String> {
}
