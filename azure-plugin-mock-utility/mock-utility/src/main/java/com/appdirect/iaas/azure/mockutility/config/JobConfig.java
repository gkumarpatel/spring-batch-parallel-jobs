package com.appdirect.iaas.azure.mockutility.config;

import lombok.RequiredArgsConstructor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class JobConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    
    @Value("${mock.chunkSize}")
    private Integer microsoftLineItemFetchCount;
    
    @Bean
    public Job job(@Qualifier("generateMocks") Step step1) {
        return jobs.get("myJob")
                .incrementer(new RunIdIncrementer())
                .start(step1).build();
    }

    @Bean
    protected Step generateMocks(ItemReader<InvoiceLineItem> invoiceItemReader,
                                 ItemProcessor<InvoiceLineItem, InvoiceLineItem> invoiceItemProcessor,
                                 ItemWriter<InvoiceLineItem> invoiceItemWriter) {
        return steps.get("generateMocks")
                .<InvoiceLineItem, InvoiceLineItem>chunk(microsoftLineItemFetchCount)
                .reader(invoiceItemReader)
                .processor(invoiceItemProcessor)
                .writer(invoiceItemWriter)
                .build();
    }
}
