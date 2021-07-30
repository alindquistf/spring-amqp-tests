package br.com.rfasioli.springamqptests.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringAmqpTestsWorkerApplication {
	public static void main(String[] args) {
		final var context = SpringApplication.run(SpringAmqpTestsWorkerApplication.class, args);
	}
}
