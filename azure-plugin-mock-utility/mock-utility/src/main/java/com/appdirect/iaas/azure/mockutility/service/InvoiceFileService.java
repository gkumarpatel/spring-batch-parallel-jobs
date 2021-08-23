package com.appdirect.iaas.azure.mockutility.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;

public interface InvoiceFileService {
    
    String generateOneTimeInvoiceResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, int oneTimeJsonFileCount) throws IOException;

    String generateDailyRatedUsageResponseFile(ObjectMapper objectMapper, boolean isLastResponse, List<InvoiceLineItem> invoiceLineItems, int dailyRatedJsonFileCount) throws IOException;
    
}
