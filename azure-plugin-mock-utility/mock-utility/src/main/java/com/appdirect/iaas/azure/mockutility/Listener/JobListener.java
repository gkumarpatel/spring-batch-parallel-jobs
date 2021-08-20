package com.appdirect.iaas.azure.mockutility.Listener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.SneakyThrows;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JobListener implements JobExecutionListener {

    public static final String DAILY_RATED_FILES_PATH = "/DailyRated/__files/";
    public static final String ONE_TIME_FILES_PATH = "/OneTime/__files/";
    public static final String DAILY_RATED_MAPPINGS_PATH = "/DailyRated/mappings/";
    public static final String ONE_TIME_MAPPINGS_PATH = "/OneTime/mappings/";
    public static final String USAGE_SEPERATER = " Usages_";
    
    @Value("${responseFolder}")
    private String responseOutputPath;

    @Value("${mock.numberOfLineItems}")
    private Long numberOfLineItems;


    /**
     * Callback before a job executes.
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    @SneakyThrows
    public void beforeJob(JobExecution jobExecution) {
        generateFolderPath(DAILY_RATED_FILES_PATH);
        generateFolderPath(ONE_TIME_FILES_PATH);
        generateFolderPath(DAILY_RATED_MAPPINGS_PATH);
        generateFolderPath(ONE_TIME_MAPPINGS_PATH);
    }

    /**
     * Callback after completion of a job. Called after both both successful and
     * failed executions. To perform logic on a particular status, use
     * "if (jobExecution.getStatus() == BatchStatus.X)".
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    public void afterJob(JobExecution jobExecution) {

    }

    @SneakyThrows
    private void generateFolderPath(String filePath) {
        StringBuilder stringBuilder = new StringBuilder(responseOutputPath);

        String dailyRatedFilesFolderPath = stringBuilder.append(filePath)
                .append(numberOfLineItems.toString())
                .append(USAGE_SEPERATER)
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy'T'hh.mm.ss"))).toString();
        Files.createDirectories(Paths.get(dailyRatedFilesFolderPath));
    }
}
