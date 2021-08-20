package com.appdirect.iaas.azure.mockutility.model;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microsoft.store.partnercenter.models.invoices.BillingProvider;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItemType;

@Data

/**
 * Contains all the fields that are not part of DailyRatedUsageLineItem but not present in 
 * microsoft partner center api response
 */
public class DailyRatedUsageLineItemBean {
    private Map<String, Object> additionalInfo;
    private String availabilityId;
    private String billingCurrency;
    private double billingPreTaxTotal;
    private String customerDomainName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date chargeEndDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date chargeStartDate;
    private String chargeType;
    private String consumedService;
    private String customerCountry;
    private String customerId;
    private String customerName;
    private double effectiveUnitPrice;
    private String entitlementDescription;
    private String entitlementId;
    private String invoiceNumber;
    private String meterCategory;
    private String meterId;
    private String meterName;
    private String meterRegion;
    private String meterSubCategory;
    private String meterType;
    private String mpnId;
    private String partnerId;
    private String partnerName;
    private double pcToBCExchangeRate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date pcToBCExchangeRateDate;
    private String pricingCurrency;
    private double pricingPreTaxTotal;
    private String productId;
    private String productName;
    private String publisherId;
    private String publisherName;
    private double quantity;
    private String resellerMpnId;
    private double rateOfPartnerEarnedCredit;
    private boolean hasPartnerEarnedCredit;
    private String resourceGroup;
    private String resourceLocation;
    private String resourceUri;
    private String serviceInfo1;
    private String serviceInfo2;
    private String skuId;
    private String skuName;
    private String subscriptionDescription;
    private String subscriptionId;
    private Map<String, Object> tags;
    private String unitOfMeasure;
    private double unitPrice;
    private String unitType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date usageDate;
    private String creditType;
    private String rateOfCredit;
    private InvoiceLineItemType invoiceLineItemType = InvoiceLineItemType.USAGELINEITEMS;
    private BillingProvider billingProvider = BillingProvider.MARKETPLACE;
    private Map<String, String> attributes = Collections.singletonMap("objectType", "DailyRatedUsageLineItem");
}
