package com.appdirect.iaas.azure.mockutility.model;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microsoft.store.partnercenter.models.invoices.BillingProvider;
import com.microsoft.store.partnercenter.models.invoices.InvoiceLineItemType;

/**
 * Contains all the fields that are not part of OneTimeInvoiceLineItem but present in 
 * microsoft partner center api response
 */
@Data
public class OneTimeInvoiceLineItemBean {
    private String alternateId;
    private String availabilityId;
    private String billingFrequency;
    private double billableQuantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date chargeEndDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date chargeStartDate;
    private String chargeType;
    private String currency;
    private String customerCountry;
    private String customerDomainName;
    private String customerId;
    private String customerName;
    private String discountDetails;
    private String invoiceNumber;
    private String meterDescription;
    private String mpnId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date orderDate;
    private String orderId;
    private String partnerId;
    private double pcToBCExchangeRate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'")
    private Date pcToBCExchangeRateDate;
    private String priceAdjustmentDescription;
    private String pricingCurrency;
    private String productId;
    private String productName;
    private String publisherId;
    private String publisherName;
    private int quantity;
    private String resellerMpnId = "0";
    private String reservationOrderId;
    private String skuId;
    private String skuName;
    private String subscriptionDescription;
    private String subscriptionId;
    private double subtotal;
    private double taxTotal;
    private String termAndBillingCycle;
    private double totalForCustomer;
    private double unitPrice;
    private double effectiveUnitPrice;
    private String unitType;
    private String referenceId = "";
    private InvoiceLineItemType invoiceLineItemType = InvoiceLineItemType.BILLINGLINEITEMS;
    private BillingProvider billingProvider = BillingProvider.ONE_TIME;
    private String promotionId = "";
    private Map<String, String> attributes = Collections.singletonMap("objectType", "OneTimeInvoiceLineItem"); 
}
