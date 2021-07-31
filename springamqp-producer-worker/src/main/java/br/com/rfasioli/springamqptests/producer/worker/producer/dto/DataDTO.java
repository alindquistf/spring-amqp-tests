package br.com.rfasioli.springamqptests.producer.worker.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName="of")
public class DataDTO {
    private String id;
    private Long count;
}
