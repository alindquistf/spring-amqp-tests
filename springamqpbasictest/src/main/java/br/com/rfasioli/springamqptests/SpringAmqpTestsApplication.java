package br.com.rfasioli.springamqptests;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAmqpTestsApplication {

	public static void main(String[] args) {
		final var context = SpringApplication.run(SpringAmqpTestsApplication.class, args);
		context.close();
	}

	@Bean
	public ApplicationRunner runner(final DemoService service) {
		return args -> service.run();
	}

}
