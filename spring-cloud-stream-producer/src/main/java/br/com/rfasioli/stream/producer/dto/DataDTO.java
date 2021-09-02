package br.com.rfasioli.stream.producer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName="of")
public class DataDTO {
    private String id;
    private Long count;
}
