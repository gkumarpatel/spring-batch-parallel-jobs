package com.appdirect.iaas.azure.mockutility.model;

import java.util.Collections;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import com.microsoft.store.partnercenter.models.invoices.BillingProvider;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItemType;
import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;


/**
 * Contains all the fields that are not part of OneTimeInvoiceLineItem but present in 
 * microsoft partner center api response
 */
@Getter
@Setter
public class ExtendedOneTimeInvoiceLineItem extends OneTimeInvoiceLineItem {
    private String referenceId = "";
    private InvoiceLineItemType invoiceLineItemType = InvoiceLineItemType.BILLINGLINEITEMS;
    private BillingProvider billingProvider = BillingProvider.ONE_TIME;
    private String promotionId = "";
    private Map<String, String> atributes = Collections.singletonMap("objectType", "OneTimeInvoiceLineItem");
}
