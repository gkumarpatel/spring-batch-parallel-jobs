package com.appdirect.iaas.azure.mockutility.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;

public interface InvoiceFileService {
    
    void generateOneTimeInvoiceResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, String oneTimeInvoiceFilesPath, int oneTimeJsonFileCount) throws IOException;

    void generateDailyRatedUsageResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, String dailyRatedInvoiceFilesPath, int dailyRatedJsonFileCount) throws IOException;
    
}
