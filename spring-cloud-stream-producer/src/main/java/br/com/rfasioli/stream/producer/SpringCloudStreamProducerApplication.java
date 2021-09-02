package br.com.rfasioli.stream.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringCloudStreamProducerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringCloudStreamProducerApplication.class, args);
    }

}