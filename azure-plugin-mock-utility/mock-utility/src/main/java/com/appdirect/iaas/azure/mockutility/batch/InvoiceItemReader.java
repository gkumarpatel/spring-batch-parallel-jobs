package com.appdirect.iaas.azure.mockutility.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.appdirect.iaas.azure.mockutility.mapper.OneTimeInvoiceLineItemMapper;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemCSV;
import com.microsoft.store.partnercenter.models.invoices.DailyRatedUsageLineItem;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;
import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;
import com.opencsv.bean.CsvToBeanBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceItemReader implements ItemStreamReader<InvoiceLineItem> {

    private final OneTimeInvoiceLineItemMapper oneTimeInvoiceLineItemMapper;

    @Value("classpath:${reconcillation.oneTime.fileName}")
    private Resource oneTimeFileResource;

    @Value("${mock.numberOfLineItems}")
    private Long numberOfLineItems;

    @Value("${mock.chunkSize}")
    private Integer chunkSize;

    private Long numberOfLineItemsRead = 0l;

    private Boolean isCurrentListDailyRated = false;

    private int dailyRatedIndex = 0;
    private int oneTimeIndex = 0;

    private List<DailyRatedUsageLineItem> dailyRatedInvoiceLineItem;

    private List<OneTimeInvoiceLineItem> oneTimeInvoiceLineItem;

    @Override
    public InvoiceLineItem read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (numberOfLineItemsRead == numberOfLineItems) {
            return null;
        }

        InvoiceLineItem invoiceLineItem;
        if (isCurrentListDailyRated) {
            invoiceLineItem = dailyRatedInvoiceLineItem.get(dailyRatedIndex % dailyRatedInvoiceLineItem.size());
            dailyRatedIndex++;

            if (dailyRatedIndex % chunkSize == 0) {
                isCurrentListDailyRated = false;
                dailyRatedIndex = 0;
            }
        } else {
            invoiceLineItem = oneTimeInvoiceLineItem.get(oneTimeIndex % oneTimeInvoiceLineItem.size());
            oneTimeIndex++;

            if (oneTimeIndex % chunkSize == 0) {
                isCurrentListDailyRated = true;
                oneTimeIndex = 0;
            }
        }
        numberOfLineItemsRead++;

        return invoiceLineItem;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info(" Generating the subscriptions from properties file {} ", oneTimeFileResource.getFilename());
        try {
            List<OneTimeInvoiceLineItemCSV> oneTimeInvoiceLineItemCsvList = new CsvToBeanBuilder(new BufferedReader(new InputStreamReader(oneTimeFileResource.getInputStream())))
                    .withType(OneTimeInvoiceLineItemCSV.class)
                    .build()
                    .parse();
            oneTimeInvoiceLineItem = oneTimeInvoiceLineItemCsvList.stream().map(oneTimeInvoiceLineItemMapper::mapFromOneTimeInvoiceLineItemCSV).collect(Collectors.toList());
            dailyRatedInvoiceLineItem = new ArrayList<>();
        } catch (IOException e) {
            log.error("Error while reading the subscriptions from properties file {}, {}", oneTimeFileResource.getFilename(), e.getMessage());
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}
