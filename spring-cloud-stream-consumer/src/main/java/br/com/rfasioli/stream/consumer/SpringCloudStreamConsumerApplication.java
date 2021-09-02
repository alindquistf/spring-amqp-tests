package br.com.rfasioli.stream.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringCloudStreamConsumerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringCloudStreamConsumerApplication.class, args);
    }

}
