package com.appdirect.iaas.azure.mockutility.model;

import java.time.LocalDate;

import lombok.Data;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;

@Data
public class DailyRatedUsageLineItemCSV {

    @CsvBindByName(column = "AdditionalInfo")
    private String additionalInfo;

    @CsvBindByName(column = "AvailabilityId")
    private String availabilityId;

    @CsvBindByName(column = "BillingCurrency")
    private String billingCurrency;

    @CsvBindByName(column = "BillingPreTaxTotal")
    private double billingPreTaxTotal;

    @CsvBindByName(column = "CustomerDomainName")
    private String customerDomainName;

    @CsvBindByName(column = "ChargeEndDate")
    @CsvDate(value = "MM/dd/yyyy")
    private LocalDate chargeEndDate;

    @CsvBindByName(column = "ChargeStartDate")
    @CsvDate(value = "MM/dd/yyyy")
    private LocalDate chargeStartDate;

    @CsvBindByName(column = "ChargeType")
    private String chargeType;

    @CsvBindByName(column = "ConsumedService")
    private String consumedService;

    @CsvBindByName(column = "CustomerCountry")
    private String customerCountry;

    @CsvBindByName(column = "CustomerId")
    private String customerId;

    @CsvBindByName(column = "CustomerName")
    private String customerName;

    @CsvBindByName(column = "EffectiveUnitPrice")
    private double effectiveUnitPrice;

    @CsvBindByName(column = "EntitlementDescription")
    private String entitlementDescription;

    @CsvBindByName(column = "EntitlementId")
    private String entitlementId;

    @CsvBindByName(column = "InvoiceNumber")
    private String invoiceNumber;

    @CsvBindByName(column = "MeterCategory")
    private String meterCategory;

    @CsvBindByName(column = "MeterId")
    private String meterId;

    @CsvBindByName(column = "MeterName")
    private String meterName;

    @CsvBindByName(column = "MeterRegion")
    private String meterRegion;

    @CsvBindByName(column = "MeterSubCategory")
    private String meterSubCategory;

    @CsvBindByName(column = "MeterType")
    private String meterType;

    @CsvBindByName(column = "MpnId")
    private String mpnId;

    @CsvBindByName(column = "PartnerId")
    private String partnerId;

    @CsvBindByName(column = "PartnerName")
    private String partnerName;

    @CsvBindByName(column = "PCToBCExchangeRate")
    private double pcToBCExchangeRate;

    @CsvBindByName(column = "PCToBCExchangeRateDate")
    @CsvDate(value = "MM/dd/yyyy")
    private LocalDate pcToBCExchangeRateDate;

    @CsvBindByName(column = "PricingCurrency")
    private String pricingCurrency;

    @CsvBindByName(column = "PricingPreTaxTotal")
    private double pricingPreTaxTotal;

    @CsvBindByName(column = "ProductId")
    private String productId;

    @CsvBindByName(column = "ProductName")
    private String productName;

    @CsvBindByName(column = "PublisherId")
    private String publisherId;

    @CsvBindByName(column = "PublisherName")
    private String publisherName;

    @CsvBindByName(column = "Quantity")
    private double quantity;

    @CsvBindByName(column = "ResellerMpnId")
    @CsvIgnore
    private String resellerMpnId;

    @CsvBindByName(column = "RateOfPartnerEarnedCredit")
    @CsvIgnore
    private double rateOfPartnerEarnedCredit;

    @CsvBindByName(column = "HasPartnerEarnedCredit")
    @CsvIgnore
    private boolean hasPartnerEarnedCredit;

    @CsvBindByName(column = "ResourceGroup")
    private String resourceGroup;

    @CsvBindByName(column = "ResourceLocation")
    private String resourceLocation;

    @CsvBindByName(column = "ResourceUri")
    private String resourceUri;

    @CsvBindByName(column = "ServiceInfo1")
    private String serviceInfo1;

    @CsvBindByName(column = "ServiceInfo2")
    private String serviceInfo2;

    @CsvBindByName(column = "SkuId")
    private String skuId;

    @CsvBindByName(column = "SkuName")
    private String skuName;

    @CsvBindByName(column = "SubscriptionDescription")
    private String subscriptionDescription;

    @CsvBindByName(column = "SubscriptionId")
    private String subscriptionId;

    @CsvBindByName(column = "Tags")
    private String tags;

    @CsvBindByName(column = "UnitOfMeasure")
    @CsvIgnore
    private String unitOfMeasure;
    
    @CsvBindByName(column = "UnitPrice")
    private double unitPrice;

    @CsvBindByName(column = "UnitType")
    private String unitType;

    @CsvBindByName(column = "UsageDate")
    @CsvDate(value = "MM/dd/yyyy")
    private LocalDate usageDate;
}
