package com.appdirect.iaas.azure.mockutility.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateMockJobLauncher implements CommandLineRunner {

    @Value("reconcillation.oneTime.fileName")
    private String oneTimeReconcillationFileName;

    @Value("reconcillation.oneTime.fileName")
    private String dailyRatedReconcillationFileName;

    private final JobLauncher jobLauncher;
    
    private final Job generateMocks;

    public void launchGenerateMockJob() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

        jobParametersBuilder.addString("oneTimeReconcillationFileName", oneTimeReconcillationFileName)
                .addString("dailyRatedReconcillationFileName", dailyRatedReconcillationFileName)
                .toJobParameters();
        try {
            jobLauncher.run(generateMocks, jobParametersBuilder.toJobParameters());
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run(String... args) throws Exception {
        launchGenerateMockJob();
    }
}
