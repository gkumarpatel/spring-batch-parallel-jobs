package com.appdirect.iaas.azure.mockutility.mapper;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageLineItemBean;
import com.appdirect.iaas.azure.mockutility.model.DailyRatedUsageLineItemCSV;
import com.microsoft.store.partnercenter.models.invoices.DailyRatedUsageLineItem;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
@Slf4j
public class DailyRatedUsageLineItemMapper {
    private MapperFacade mapperFacade;
    private MapperFacade mapperFacade2;

    @PostConstruct
    public void init() {
        var mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(DailyRatedUsageLineItemCSV.class, DailyRatedUsageLineItem.class).byDefault().register();
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new LocalDateAndJodaDateConverter());
        converterFactory.registerConverter(new StringToMapConverter());
        mapperFacade = mapperFactory.getMapperFacade();

        var mapperFactory2 = new DefaultMapperFactory.Builder().mapNulls(false).build();
        mapperFactory2.getConverterFactory().registerConverter(new JodaDateAndLocalDateTimeConverter());
        mapperFactory2.classMap(DailyRatedUsageLineItem.class, DailyRatedUsageLineItemBean.class)
                .fieldAToB("additionalInfo", "additionalInfo").fieldAToB("availabilityId", "availabilityId")
                .fieldAToB("billingCurrency", "billingCurrency").fieldAToB("billingPreTaxTotal", "billingPreTaxTotal")
                .fieldAToB("customerDomainName", "customerDomainName").fieldAToB("chargeEndDate", "chargeEndDate")
                .fieldAToB("chargeStartDate", "chargeStartDate").fieldAToB("chargeType", "chargeType")
                .fieldAToB("consumedService", "consumedService").fieldAToB("customerCountry", "customerCountry")
                .fieldAToB("customerId", "customerId").fieldAToB("customerName", "customerName")
                .fieldAToB("effectiveUnitPrice", "effectiveUnitPrice").fieldAToB("entitlementDescription", "entitlementDescription")
                .fieldAToB("entitlementId", "entitlementId").fieldAToB("invoiceNumber", "invoiceNumber")
                .fieldAToB("meterCategory", "meterCategory").fieldAToB("meterId", "meterId")
                .fieldAToB("meterName", "meterName").fieldAToB("meterRegion", "meterRegion")
                .fieldAToB("meterSubCategory", "meterSubCategory").fieldAToB("meterType", "meterType")
                .fieldAToB("mpnId", "mpnId").fieldAToB("partnerId", "partnerId")
                .fieldAToB("partnerName", "partnerName").fieldAToB("PCToBCExchangeRate", "pcToBCExchangeRate")
                .fieldAToB("PCToBCExchangeRateDate", "pcToBCExchangeRateDate").fieldAToB("pricingCurrency", "pricingCurrency")
                .fieldAToB("pricingPreTaxTotal", "pricingPreTaxTotal").fieldAToB("productId", "productId")
                .fieldAToB("productName", "productName").fieldAToB("publisherId", "publisherId")
                .fieldAToB("publisherName", "publisherName").fieldAToB("quantity", "quantity")
                .fieldAToB("resellerMpnId", "resellerMpnId").fieldAToB("rateOfPartnerEarnedCredit", "rateOfPartnerEarnedCredit")
                .fieldAToB("hasPartnerEarnedCredit", "hasPartnerEarnedCredit").fieldAToB("resourceGroup", "resourceGroup")
                .fieldAToB("resourceUri", "resourceUri").fieldAToB("resourceLocation", "resourceLocation")
                .fieldAToB("serviceInfo1", "serviceInfo1").fieldAToB("serviceInfo2", "serviceInfo2")
                .fieldAToB("skuId", "skuId").fieldAToB("skuName", "skuName")
                .fieldAToB("subscriptionDescription", "subscriptionDescription").fieldAToB("subscriptionId", "subscriptionId")
                .register();

        mapperFacade2 = mapperFactory2.getMapperFacade();
    }

    public DailyRatedUsageLineItem mapFromDailyRatedInvoiceLineItemCSV(DailyRatedUsageLineItemCSV dailyRatedUsageLineItemCSV) {
        try {
            return mapperFacade.map(dailyRatedUsageLineItemCSV, DailyRatedUsageLineItem.class);
        } catch (Exception e) {
            log.error("exception while mapping={}", e);
        }
        return null;
    }

    public DailyRatedUsageLineItemBean mapFromDailyRatedInvoiceLineItem(DailyRatedUsageLineItem dailyRatedUsageLineItem) {
        try {
            return mapperFacade2.map(dailyRatedUsageLineItem, DailyRatedUsageLineItemBean.class);
        } catch (Exception e) {
            log.error("exception while mapping={}", e);
        }
        return null;
    }
}
