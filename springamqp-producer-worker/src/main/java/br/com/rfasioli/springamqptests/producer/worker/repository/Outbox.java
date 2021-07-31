package br.com.rfasioli.springamqptests.producer.worker.repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Outbox {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Indexed
    @NonNull
    private String correlation;

    @NonNull
    private String payload;

    @NonNull
    private STATUS status;

    private STATUS consumerStatus;

    @NonNull
    private TYPE type;

    public enum STATUS {
        PENDING,
        SENT,
        NACK,
        ERROR,
        PROCESSED
    }

    public enum TYPE {
        CLASSIC,
        CONFIRM_RETURNS,
        QUORUM
    }
}
