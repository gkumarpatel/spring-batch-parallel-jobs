package com.appdirect.iaas.azure.mockutility.batch;

import java.util.List;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.appdirect.iaas.azure.mockutility.service.InvoiceFileService;
import com.appdirect.iaas.azure.mockutility.service.MappingFileService;
import com.appdirect.iaas.azure.mockutility.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.microsoft.store.partnercenter.models.invoices.DailyRatedUsageLineItem;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;
import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;

@Component
@Slf4j
@RequiredArgsConstructor
public class InvoiceItemWriter implements ItemWriter<InvoiceLineItem> {

    private static Long lineItemsToWrite;
    private static String oneTimeInvoiceFilesPath;
    private static String oneTimeMappingFilesPath;
    private static String dailyRatedInvoiceFilesPath;
    private static String dailyRatedMappingFilesPath;

    private static int oneTimeJsonFileCount = 1;
    private static int dailyRatedJsonFileCount = 1;

    private final InvoiceFileService invoiceFileService;
    private final MappingFileService mappingFileService;

    @Value("${mock.numberOfLineItems}")
    private Long numberOfLineItems;

    @Value("${responseFolder.dailyRatedPath.filesPath}")
    public String dailyRatedFilesPath;

    @Value("${responseFolder.OneTimePath.filesPath}")
    public String oneTimeFilesPath;

    @Value("${responseFolder.mainFolderPath}")
    private String responseOutputPath;

    @Value("${responseFolder.dailyRatedPath.mappingPath}")
    public String dailyRatedMappingsPath;

    @Value("${responseFolder.OneTimePath.mappingPath}")
    public String oneTimeMappingsPath;


    @PostConstruct
    public void setUp() {
        lineItemsToWrite = numberOfLineItems;
        String totalInvoices = numberOfLineItems.toString();
        oneTimeInvoiceFilesPath = FileUtil.generateFolders(oneTimeFilesPath, responseOutputPath, totalInvoices).toString();
        oneTimeMappingFilesPath = FileUtil.generateFolders(oneTimeMappingsPath, responseOutputPath, totalInvoices).toString();
        dailyRatedInvoiceFilesPath = FileUtil.generateFolders(dailyRatedFilesPath, responseOutputPath, totalInvoices).toString();
        dailyRatedMappingFilesPath = FileUtil.generateFolders(dailyRatedMappingsPath, responseOutputPath, totalInvoices).toString();
    }

    @Override
    public void write(List<? extends InvoiceLineItem> items) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        boolean isLastResponse = isLastResponse(items);

        InvoiceLineItem invoiceLineItem = items.get(0);

        String pageSize = String.valueOf(items.size());

        if (invoiceLineItem instanceof OneTimeInvoiceLineItem) {
            invoiceFileService.generateOneTimeInvoiceResponseFile(objectMapper, isLastResponse, (List<InvoiceLineItem>) items, oneTimeInvoiceFilesPath, oneTimeJsonFileCount);
            mappingFileService.generateOneTimeInvoiceMappingFile(objectMapper, oneTimeJsonFileCount, ((OneTimeInvoiceLineItem) invoiceLineItem).getInvoiceNumber(), pageSize, oneTimeMappingFilesPath);
            oneTimeJsonFileCount++;
        } else {
            invoiceFileService.generateDailyRatedUsageResponseFile(objectMapper, isLastResponse, (List<InvoiceLineItem>) items, dailyRatedInvoiceFilesPath, dailyRatedJsonFileCount);
            mappingFileService.generateDailyRatedUsageMappingFile(objectMapper, dailyRatedJsonFileCount, ((DailyRatedUsageLineItem) invoiceLineItem).getInvoiceNumber(), pageSize, dailyRatedMappingFilesPath);
            dailyRatedJsonFileCount++;
        }

        lineItemsToWrite -= items.size();
    }

    private boolean isLastResponse(List<? extends InvoiceLineItem> items) {
        boolean isLastResponse = false;
        
        if (lineItemsToWrite <= (2 * items.size())) {
            isLastResponse = true;
        }
        return isLastResponse;
    }
}
