package br.com.rfasioli.springamqptests.worker.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName="of")
@NoArgsConstructor
public class DataDTO {
    private String id;
    private Long count;
}
