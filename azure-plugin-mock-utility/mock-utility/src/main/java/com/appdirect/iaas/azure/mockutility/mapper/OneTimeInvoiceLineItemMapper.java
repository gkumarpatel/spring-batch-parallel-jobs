package com.appdirect.iaas.azure.mockutility.mapper;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemCSV;
import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
@Slf4j
public class OneTimeInvoiceLineItemMapper {
    private MapperFacade mapperFacade;
    private MapperFacade mapperFacade2;

    @PostConstruct
    public void init() {
        var mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).build();
        mapperFactory.classMap(OneTimeInvoiceLineItemCSV.class, OneTimeInvoiceLineItem.class).byDefault().register();
        mapperFactory.getConverterFactory().registerConverter(new LocalDateAndJodaDateConverter());
        mapperFacade = mapperFactory.getMapperFacade();

        var mapperFactory2 = new DefaultMapperFactory.Builder().mapNulls(false).build();
        mapperFactory2.getConverterFactory().registerConverter(new JodaDateAndLocalDateTimeConverter());
        mapperFactory2.classMap(OneTimeInvoiceLineItem.class, OneTimeInvoiceLineItemBean.class)
                .fieldAToB("alternateId", "alternateId").fieldAToB("availabilityId", "availabilityId")
                .fieldAToB("billingFrequency", "billingFrequency").fieldAToB("chargeEndDate", "chargeEndDate")
                .fieldAToB("billableQuantity", "billableQuantity").fieldAToB("chargeStartDate", "chargeStarDate")
                .fieldAToB("chargeType", "chargeType").fieldAToB("currency", "currency")
                .fieldAToB("customerCountry", "customerCountry").fieldAToB("customerDomainName", "customerDomainName")
                .fieldAToB("customerName", "customerName").fieldAToB("customerId", "customerId")
                .fieldAToB("discountDetails", "discountDetails").fieldAToB("invoiceNumber", "invoiceNumber")
                .fieldAToB("mpnId", "mpnId").fieldAToB("meterDescription", "meterDescription")
                .fieldAToB("orderDate", "orderDate").fieldAToB("orderId", "orderId")
                .fieldAToB("partnerId", "partnerId").fieldAToB("PCToBCExchangeRate", "pcToBCExchangeRate")
                .fieldAToB("PCToBCExchangeRateDate", "pcToBCExchangeRateDate").fieldAToB("productId", "productId")
                .fieldAToB("priceAdjustmentDescription", "priceAdjustmentDescription").fieldAToB("productName", "productName")
                .fieldAToB("pricingCurrency", "pricingCurrency").fieldAToB("publisherId", "publisherId")
                .fieldAToB("publisherName", "publisherName").fieldAToB("quantity", "quantity")
                .fieldAToB("resellerMpnId", "resellerMpnId").fieldAToB("reservationOrderId", "reservationOrderId")
                .fieldAToB("skuId", "skuId").fieldAToB("subscriptionDescription", "subscriptionDescription")
                .fieldAToB("skuName", "skuName").fieldAToB("subscriptionId", "subscriptionId")
                .fieldAToB("subtotal","subtotal").fieldAToB("taxTotal", "taxTotal")
                .fieldAToB("termAndBillingCycle","termAndBillingCycle").fieldAToB("totalForCustomer", "totalForCustomer")
                .fieldAToB("unitPrice","unitPrice").fieldAToB("effectiveUnitPrice", "effectiveUnitPrice")
                .fieldAToB("unitType","unitType").register();
        mapperFacade2 = mapperFactory2.getMapperFacade();
    }

    public OneTimeInvoiceLineItem mapFromOneTimeInvoiceLineItemCSV(OneTimeInvoiceLineItemCSV oneTimeInvoiceLineItemCSV) {
        try {
            return mapperFacade.map(oneTimeInvoiceLineItemCSV, OneTimeInvoiceLineItem.class);
        } catch (Exception e) {
            log.error("exception while mapping={}", e);
        }
        return null;
    }

    public OneTimeInvoiceLineItemBean mapFromOneTimeInvoiceLineItem(OneTimeInvoiceLineItem oneTimeInvoiceLineItem) {
        try {
            return mapperFacade2.map(oneTimeInvoiceLineItem, OneTimeInvoiceLineItemBean.class);
        } catch (Exception e) {
            log.error("exception while mapping={}", e);
        }
        return null;
    }
}
