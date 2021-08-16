package com.appdirect.iaas.azure.mockutility.model;

import lombok.Data;

import org.joda.time.DateTime;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;

@Data
public class OneTimeInvoiceLineItemCSV {

    @CsvBindByName(column = "AlternateId")
    private String alternateId;

    @CsvBindByName(column = "AvailabilityId")
    private String availabilityId;

    @CsvBindByName(column = "BillingFrequency")
    private String billingFrequency;

    @CsvBindByName(column = "BillableQuantity")
    private double billableQuantity;

    @CsvDate(value = "MM/dd/yyyy")
    @CsvBindByName(column = "ChargeEndDate")
    private DateTime chargeEndDate;

    @CsvDate(value = "MM/dd/yyyy")
    @CsvBindByName(column = "ChargeStartDate")
    private DateTime chargeStarDate;

    @CsvBindByName(column = "ChargeType")
    private String chargeType;

    @CsvBindByName(column = "Currency")
    private String currency;

    @CsvBindByName(column = "CustomerCountry")
    private String customerCountry;

    @CsvBindByName(column = "CustomerDomainName")
    private String customerDomainName;

    @CsvBindByName(column = "customerId")
    private String customerId;

    @CsvBindByName(column = "CustomerName")
    private String customerName;

    @CsvIgnore
    @CsvBindByName(column = "DiscountDetails")
    private String discountDetails;

    @CsvBindByName(column = "InvoiceNumber")
    private String invoiceNumber;

    @CsvBindByName(column = "MeterDescription")
    private String meterDescription;

    @CsvBindByName(column = "MpnId")
    private String mpnId;

    @CsvBindByName(column = "OrderDate")
    @CsvDate(value = "MM/dd/yyyy")
    private DateTime orderDate;

    @CsvBindByName(column = "OrderId")
    private String orderId;

    @CsvBindByName(column = "PartnerId")
    private String partnerId;

    @CsvBindByName(column = "PCToBCExchangeRate")
    private double pcToBCExchangeRate;

    @CsvBindByName(column = "PCToBCExchangeRateDate")
    @CsvDate(value = "MM/dd/yyyy")
    private DateTime pcToBCExchangeRateDate;

    @CsvBindByName(column = "PriceAdjustmentDescription")
    private String priceAdjustmentDescription;

    @CsvBindByName(column = "PricingCurrency")
    private String pricingCurrency;

    @CsvBindByName(column = "ProductId")
    private String productId;

    @CsvBindByName(column = "ProductName")
    private String productName;

    @CsvBindByName(column = "PublisherId")
    private String publisherId;

    @CsvBindByName(column = "PublisherName")
    private String publisherName;

    @CsvBindByName(column = "Quantity")
    private int quantity;

    @CsvBindByName(column = "ResellerMpnId")
    @CsvIgnore
    private String resellerMpnId;

    @CsvBindByName(column = "ReservationOrderId")
    private String reservationOrderId;

    @CsvBindByName(column = "SkuId")
    private String skuId;

    @CsvBindByName(column = "SkuName")
    private String skuName;

    @CsvBindByName(column = "SubscriptionDescription")
    private String subscriptionDescription;

    @CsvBindByName(column = "SubscriptionId")
    private String subscriptionId;

    @CsvBindByName(column = "Subtotal")
    private double subtotal;

    @CsvBindByName(column = "TaxTotal")
    private double taxTotal;

    @CsvBindByName(column = "TermAndBillingCycle")
    private String termAndBillingCycle;

    @CsvBindByName(column = "TotalForCustomer")
    @CsvIgnore
    private double totalForCustomer;

    @CsvBindByName(column = "UnitPrice")
    private double unitPrice;

    @CsvBindByName(column = "EffectiveUnitPrice")
    private double effectiveUnitPrice;

    @CsvBindByName(column = "UnitType")
    private String unitType;
}
