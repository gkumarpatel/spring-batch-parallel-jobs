package com.appdirect.iaas.azure.mockutility.mapper;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.appdirect.iaas.azure.mockutility.model.OneTimeInvoiceLineItemCSV;
import com.microsoft.store.partnercenter.models.invoices.OneTimeInvoiceLineItem;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
@Slf4j
public class OneTimeInvoiceLineItemMapper {
    private MapperFacade mapperFacade;

    @PostConstruct
    public void init() {
        var mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(OneTimeInvoiceLineItemCSV.class, OneTimeInvoiceLineItem.class).byDefault().register();
        mapperFactory.getConverterFactory().registerConverter(new DateConverter());
        mapperFacade = mapperFactory.getMapperFacade();
    }

    public OneTimeInvoiceLineItem mapFromOneTimeInvoiceLineItemCSV(OneTimeInvoiceLineItemCSV oneTimeInvoiceLineItemCSV) {
        try {
            return mapperFacade.map(oneTimeInvoiceLineItemCSV, OneTimeInvoiceLineItem.class);
        }catch (Exception e){
            log.error("exception while mapping={}", e);
        }
        return null;
    }

}
