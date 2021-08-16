package com.appdirect.iaas.azure.mockutility.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItem;

@Component
public class InvoiceItemProcessor implements ItemProcessor<InvoiceLineItem, InvoiceLineItem>{
    @Override
    public InvoiceLineItem process(InvoiceLineItem item) throws Exception {
        return null;
    }
}
