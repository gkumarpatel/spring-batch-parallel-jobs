package com.appdirect.iaas.azure.mockutility.model;

import java.util.List;

import lombok.Data;

@Data
public class OneTimeInvoiceLineItemResponse extends PartnerCenterAPIResponse {
    private List<OneTimeInvoiceLineItemBean> list;
}
