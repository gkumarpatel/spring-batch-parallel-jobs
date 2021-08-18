package com.appdirect.iaas.azure.mockutility.model;

import java.util.List;
import java.util.Map;

public class OneTimeLineItemResponse {
    private String continuationToken;
    private Integer totalCount;
    private List<ExtendedOneTimeInvoiceLineItem> list;
    private Map<String, ResourceLink> links;
}
