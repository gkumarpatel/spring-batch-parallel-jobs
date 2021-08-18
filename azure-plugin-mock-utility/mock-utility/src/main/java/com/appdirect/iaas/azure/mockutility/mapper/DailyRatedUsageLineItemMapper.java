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
        converterFactory.registerConverter(new DateConverter());
        converterFactory.registerConverter(new StringToMapConverter());
        mapperFacade = mapperFactory.getMapperFacade();

        var mapperFactory2 = new DefaultMapperFactory.Builder().build();
        mapperFactory2.classMap(DailyRatedUsageLineItem.class, DailyRatedUsageLineItemBean.class).byDefault().register();
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
