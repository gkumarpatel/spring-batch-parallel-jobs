package com.appdirect.iaas.azure.mockutility.config;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JobConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    
    @Value("${mock.chunkSize}")
    private Integer chunkSize;
    
    @Bean
    public Job job(@Qualifier("generateMocks") Step step1) {
        return jobs.get("myJob").start(step1).build();
    }

    @Bean
    protected Step generateMocks(ItemReader<Object> reader,
                                 ItemProcessor<Object, Object> processor,
                                 ItemWriter<Object> writer) {
        return steps.get("step1")
                .<Object, Object>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
