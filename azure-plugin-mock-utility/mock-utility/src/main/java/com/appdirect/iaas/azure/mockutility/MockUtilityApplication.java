package com.appdirect.iaas.azure.mockutility;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MockUtilityApplication {
	
    public static void main(String[] args) {
        SpringApplication.run(MockUtilityApplication.class, args);
    }
}
