package spring.batch.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"spring.batch.integration"})
public class Manager {
	public static void main(String[] args) {
		SpringApplication.run(Manager.class);
	}
}

