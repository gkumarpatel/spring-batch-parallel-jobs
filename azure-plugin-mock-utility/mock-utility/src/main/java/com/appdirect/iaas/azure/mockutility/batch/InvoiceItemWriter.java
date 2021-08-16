package com.appdirect.iaas.azure.mockutility.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;

@Component
public class InvoiceItemWriter implements ItemWriter<InvoiceLineItem> {
    
    @Override
    public void write(List<? extends InvoiceLineItem> items) throws Exception {
        
    }
}
