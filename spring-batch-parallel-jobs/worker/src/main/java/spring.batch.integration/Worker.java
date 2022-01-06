package spring.batch.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"spring.batch.integration"})
public class Worker {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Worker.class);
		app.run(args);
	}
}

