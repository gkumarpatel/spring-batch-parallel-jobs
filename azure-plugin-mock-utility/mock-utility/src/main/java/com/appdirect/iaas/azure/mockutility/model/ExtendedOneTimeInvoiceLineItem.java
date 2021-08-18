package com.appdirect.iaas.azure.mockutility.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Data;

import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;


/**
 * Contains all the fields that are not part of OneTimeInvoiceLineItem but present in 
 * microsoft partner center api response
 */
@Data
public class ExtendedOneTimeInvoiceLineItem extends OneTimeInvoiceLineItem {
    private String referenceId = "";
    private String invoiceLineItemType = "invoiceLineItemType";
    private String billingProvider = "one_time";
    private String promotionId = "";
    private Map<String, String> attributes = Collections.singletonMap("objectType", "OneTimeInvoiceLineItem");
}
