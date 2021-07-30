package br.com.rfasioli.springamqptests.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringAmqpTestsApiApplication {
	public static void main(String[] args) {
		final var context = SpringApplication.run(SpringAmqpTestsApiApplication.class, args);
	}
}
